package core;

import com.beust.jcommander.JCommander;
import driver.DriverConnector;
import repository.DatabaseRepository;
import services.*;
import util.YamlReadingUtil;

public class GoogleSheetsToDatabase {
    private static GoogleSheetsToDatabase instance;

    private GoogleSheetsToDatabase() {
    }

    public static void run(String[] args) {
        if (instance == null) {
            instance = new GoogleSheetsToDatabase();
        }
        instance.init(args);
    }

    private void init(String[] args) {
        //Read yml file
        YamlReadingUtil util = new YamlReadingUtil();
        //Get external yml file to read
        JCommander cmdArgs = JCommander.newBuilder().addObject(util).build();
        cmdArgs.parse(args);
        //Get DataBase connection
        DriverConnector connector = new DriverConnector(util.read());
        //Repository
        DatabaseRepository repo = new DatabaseRepository(connector);
        //start userinputservice
        UserInputService inputService = new UserInputService(repo);
        //Used to year of admission
        AdmissionYearListService yearListService = new AdmissionYearListService();
        //connects to google sheets and makes query to ge get row and then inserts data in db
        SheetReadingService readingService = new SheetReadingService();
        //SortingService
        SortingService sortingService = new YearBasedSorting(inputService, yearListService, readingService, repo);
        //point of entry
        sortingService.sortAndBatch();
        inputService.runCustomSelectQuery();

    }
}
