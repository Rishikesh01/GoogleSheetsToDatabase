package services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import domain.AdmissionYear;
import lombok.RequiredArgsConstructor;
import repository.DatabaseRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SheetReadingService {
    private static final String APPLICATION_NAME = "Desktop 1";
    private static final JsonFactory FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final DatabaseRepository repository;
    private final AdmissionYearListService yearListService;

    private String tableName;

    public  void initialize(){
        System.out.println("Enter tableName");
        Scanner sc = new Scanner(System.in);
        tableName=sc.nextLine();
        sortAndBatch();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetReadingService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private List<List<Object>> getRows() {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter sheetURL");
        String sheetURL = sc.nextLine();
        System.out.println("Enter Range");
        String range = sc.nextLine();
        sc.close();

        final NetHttpTransport HTTP_TRANSPORT;
        List<List<Object>> values;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Sheets service = new Sheets.Builder(HTTP_TRANSPORT,
                    FACTORY
                    , getCredentials(HTTP_TRANSPORT)
            ).setApplicationName(APPLICATION_NAME)
                    .build();
            ValueRange response = service.spreadsheets().values().get(sheetURL.substring(39)
                    .split("/")[0], range)
                    .execute();
            values = response.getValues();
            //Check if value is null or is empty
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                /*
                Iterate through  first row and stream them and join them using commas
                 */
                List<Object> colNameList = values.get(0);
                String colNameStr = colNameList.stream().map(x -> (String) x).collect(Collectors.joining(","));
                repository.setColumnNamesStr(colNameStr);
                return values.subList(1, values.size());
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sortAndBatch() {
        /*
        get google sheet rows.
        take user input.
        Make a Executors pool
         */
        List<List<Object>> values = getRows();

        ExecutorService executor = Executors.newFixedThreadPool(5);
        /*
        get primary key from repository and column count form first row
         */
        int pkColumnName = repository.getPrimaryKey(tableName) - 1;
        assert values != null;

        //get list of years present and make list called batch
        AdmissionYear year = (AdmissionYear) yearListService.getListOfYear(values, pkColumnName).toArray()[0];
        int yearOfAdmission = year.getYear();
        List<List<Object>> batch = new LinkedList<>();
        /*
        start iterating rows and make new list at the beginning  of iteration and add elements.
        Parse the column which is supposed to be primary key and its starting  2 letters are year in which student
        took admission to integer
         */
        for (List<Object> rows : values) {
            List<Object> currentList = new LinkedList<>();
            int currentYearInRow = Integer.parseInt(rows.get(pkColumnName).toString().substring(0, 2));
            /*
            check whether if current year in the row is same as first year of admission.
            if it is add the row to current list and add the current lis to batch.
            if it is not same then update the year of admission and and pass the batch to
            repository and run it in another thread
             */
            if (currentYearInRow != yearOfAdmission) {
                yearOfAdmission = Integer.parseInt(rows.get(pkColumnName).toString().substring(0, 2));
                executor.submit(()->repository.initialize(new LinkedList<>(batch)));
                batch.clear();
                currentList.addAll(rows);
            } else {
                currentList.addAll(rows);
                batch.add(currentList);
            }
        }
    }
}