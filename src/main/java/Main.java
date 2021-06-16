import services.DatabaseRepository;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DatabaseRepository t1 = new DatabaseRepository();//working for user table name
        //query is  Create table users(id int,name varchar(20),email varchar(30),
//            gender char(2),sport varchar(20));
        try {
            if (!t1.createTable()) {
                System.out.println("Not created");
            } else {
                System.out.println("Created Table");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        t1.insertData();
        System.out.println("Inserted Successfully");


    }

}
