import com.beust.jcommander.JCommander;
import driver.DriverConnector;
import repository.DatabaseRepository;
import services.AdmissionYearListService;
import services.SheetReadingService;
import util.YamlReadingUtil;

public class Main {
    public static void main(String[] args) {
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
        SheetReadingService readingService = new SheetReadingService(repo, yearListService);

        //point of entry
        readingService.initialize();
    }

}
