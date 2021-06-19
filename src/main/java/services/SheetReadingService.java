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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SheetReadingService {
    private static final String APPLICATION_NAME = "Desktop 1";
    private static final JsonFactory FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private final String sheetURL;
    private final String range;
    private String colNameStr;

    public SheetReadingService(String sheetURL, String range) {
        this.sheetURL = sheetURL;
        this.range = range;
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


    public void getRow() {
        final NetHttpTransport HTTP_TRANSPORT;
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
            List<List<Object>> values = response.getValues();
            //Check if value is null or is empty
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                /*
                Iterate through  first row and stream them and join them using commas
                then loop through remaining list and sort them
                 */
                List<Object> colNameList = values.get(0);
                colNameStr = colNameList.stream().map(x -> (String) x).collect(Collectors.joining(","));

                for (List<Object> row : values.subList(1, values.size())) {
                    for (int i = 0; i < row.size(); i++) {

                    }

                }
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
