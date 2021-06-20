import com.beust.jcommander.JCommander;
import driver.DriverConnector;
import repository.DatabaseRepository;
import services.SheetReadingService;
import util.YamlReadingUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        YamlReadingUtil util = new YamlReadingUtil();
        JCommander cmdArgs = JCommander
                .newBuilder()
                .addObject(util)
                .build();
        cmdArgs.parse(args);
        System.out.println("Enter quit to stop program");
        String quit = "";
        while (!quit.equals("q")) {
            DriverConnector connector = new DriverConnector(util.read());

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter sheetID");
            String sheetId = sc.nextLine();
            System.out.println("Enter Range");
            String range = sc.nextLine();

            SheetReadingService readingService = new SheetReadingService(sheetId, range);
            readingService.getRow();
            DatabaseRepository repo = new DatabaseRepository(connector);
     /*   working for user table name
        query is  Create table users(id int,name varchar(20),email varchar(30),gender char(2),sport varchar(20));
*/
            System.out.println("to make table enter 1");
            int table = sc.nextInt();
            if (table == 1) {
                if (!repo.createTable()) {
                    System.out.println("Not created");
                } else {
                    System.out.println("Created Table");
                }
                repo.insertData();
                System.out.println("Inserted Successfully");
            }
            System.out.println("Enter 1 to run custom select queries");
            int choice = sc.nextInt();
            if (choice == 1) {
                sc.nextLine();
                boolean bool = true;
                while (bool) {
                    System.out.println("Enter your select query");
                    String sql = sc.nextLine();
                    repo.queryPrinter(sql);
                    System.out.println("enter 1 to run different query");
                    int run = sc.nextInt();
                    if (run == 1) {
                        bool = true;
                        sc.nextLine();
                    }else{
                        bool = false;
                    }
                }
            }
            sc.nextLine();
            System.out.println("to quit enter q");
            quit = sc.nextLine();
        }
    }

}
