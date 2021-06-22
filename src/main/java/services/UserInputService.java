package services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.DatabaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class UserInputService {
    private static final Logger logger = LoggerFactory.getLogger(UserInputService.class);
    private final  DatabaseRepository repository;

    public void getTableCreationQuery(){
        Scanner sc = new Scanner(System.in);
        logger.info("Enter the Query to Create Table");
        String ddl = sc.nextLine();
        repository.createTable(ddl);
    }

    public void runCustomSelectQuery() {
        Scanner sc = new Scanner(System.in);
        logger.info("Enter 1 to run custom select query");
        int option = sc.nextInt();
        while (option == 1) {
            logger.info("Enter the query");
            sc.nextLine();
            String sql = sc.nextLine();
            repository.queryPrinter(sql);
            logger.info(" To run again press 1");
            option = sc.nextInt();
        }

    }
}
