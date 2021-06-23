package core;

import com.beust.jcommander.JCommander;
import driver.DriverConnector;
import repository.DatabaseRepository;
import services.*;
import util.YamlReadingUtil;

public class GoogleSheetsToDatabase {
    private static GoogleSheetsToDatabase instance;

    //Read yml file
    private final YamlReadingUtil util = new YamlReadingUtil();
    //Get external yml file to read
    private final JCommander cmdArgs = JCommander.newBuilder().addObject(util).build();
    //Get DataBase connection
    private final DriverConnector connector = new DriverConnector(util.read());
    //Repository
    private final DatabaseRepository repo = new DatabaseRepository(connector);
    //start userinputservice
    private final UserInputService inputService = new UserInputService(repo);
    //Used to year of admission
    private final AdmissionYearListService yearListService = new AdmissionYearListService();
    //connects to google sheets and makes query to ge get row and then inserts data in db
    private final SheetReadingService readingService = new SheetReadingService();
    //SortingService
    private final BatchService batchService = new BatchService(inputService, yearListService, readingService, repo);

    private GoogleSheetsToDatabase() {
    }

    public static void run(String[] args) {
        if (instance == null) {
            instance = new GoogleSheetsToDatabase();
        }
        instance.init(args);
    }

    private void init(String[] args) {
        cmdArgs.parse(args);
        batchService.batch();
        inputService.runCustomSelectQuery();
    }
}
