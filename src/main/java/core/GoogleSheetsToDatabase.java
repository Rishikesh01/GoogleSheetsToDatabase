package core;

import com.beust.jcommander.JCommander;
import driver.DriverConnector;
import repository.DatabaseRepository;
import services.AdmissionYearListService;
import services.SheetReadingService;
import services.SortingService;
import services.YearBasedSorting;
import util.YamlReadingUtil;

public class GoogleSheetsToDatabase {
    private static GoogleSheetsToDatabase instance = null;

    private GoogleSheetsToDatabase() {
    }

    public static GoogleSheetsToDatabase run(String[] args) {
        if (instance == null){
            init(args);
            return instance = new GoogleSheetsToDatabase();
        }
        return instance;
    }

    private static void init(String[] args) {
        //Read yml file
        YamlReadingUtil util = new YamlReadingUtil();
        //Get external yml file to read
        JCommander cmdArgs = JCommander.newBuilder().addObject(util).build();
        cmdArgs.parse(args);
        //Get DataBase connection
        DriverConnector connector = new DriverConnector(util.read());
        //Repository
        DatabaseRepository repo = new DatabaseRepository(connector);
        //Used to year of admission
        AdmissionYearListService yearListService = new AdmissionYearListService();
        //connects to google sheets and makes query to ge get row and then inserts data in db
        SheetReadingService readingService = new SheetReadingService();
        //SortingService
        SortingService sortingService = new YearBasedSorting(repo, yearListService, readingService);
        //point of entry
        sortingService.sortAndBatch();
    }
}
