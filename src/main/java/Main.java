import driver.DriverConnector;
import repository.DatabaseRepository;
import services.SheetReadingService;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DriverConnector connector = new DriverConnector();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter sheetID");
        String sheetId = sc.nextLine();
        System.out.println("Enter Range");
        String range = sc.nextLine();
        SheetReadingService readingService = new SheetReadingService(sheetId,range);
        readingService.getRow();


        DatabaseRepository repo = new DatabaseRepository(connector);
        //working for user table name
        //query is  Create table users(id int,name varchar(20),email varchar(30),gender char(2),sport varchar(20));
        if (!repo.createTable()) {
            System.out.println("Not created");
        } else {
            System.out.println("Created Table");
        }

        repo.insertData();
        System.out.println("Inserted Successfully");


    }

}
