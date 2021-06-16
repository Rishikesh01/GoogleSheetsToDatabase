package services;

import testing.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
/*
1.user input will data with datatype writes query create table();
2.I need to store record in database with (columns);
3.separate tables with there year;
4.
 */

public class DatabaseRepository {
    private String str[];
    private int clen;
    private Connection connection = null;

    public DatabaseRepository() {

    }

    public DatabaseRepository(int clen) {
        this.clen = clen;
    }

    private void getConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/practise",
                    "root", "password");

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public Boolean createTable() throws SQLException, SQLSyntaxErrorException {
        getConnection();
        System.out.println("Enter the Query to Create Table");
        Scanner sc = new Scanner(System.in);
        String query = sc.nextLine();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Check Syntax");
            return false;
        }
        System.out.println("Table Created in DataBase");
        connection.close();
        return true;
    }

    //    public void insertData() throws SQLException {
//        getConnection();
//        String query="insert into student(id,name) values(?,?)";
//        PreparedStatement pt= connection.prepareStatement(query);
//        pt.setString(1, "1");
//        pt.setString(2,"harry");
//
//        pt.executeUpdate();
//
//    }
    public void insertData() {
        List<User> list = new ArrayList<>();
        list.add(new User(100, "Rishikesh", "denial@gmail.com", "M", "123"));
        list.add(new User(200, "Nk Rahul", "rocky@gmail.com", "M", "123"));
        list.add(new User(300, "Surya", "steve@gmail.com", "F", "123"));
        list.add(new User(400, "Tim", "ramesh@gmail.com", "M", "123"));

        String INSERT_USERS_SQL = "INSERT INTO user" + "  (id, name, email, gender, sport) VALUES " +
                " (?, ?, ?, ?, ?);";

        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/practise?useSSL=false", "root", "password");
             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            connection.setAutoCommit(false);
            for (Iterator<User> iterator = list.iterator(); iterator.hasNext(); ) {
                User user = (User) iterator.next();
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getGender());
                preparedStatement.setString(5, user.getSport());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (BatchUpdateException batchUpdateException) {
            printBatchUpdateException(batchUpdateException);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    public static void printBatchUpdateException(BatchUpdateException b) {

        System.err.println("----BatchUpdateException----");
        System.err.println("SQLState:  " + b.getSQLState());
        System.err.println("Message:  " + b.getMessage());
        System.err.println("Vendor:  " + b.getErrorCode());
        int[] updateCounts = b.getUpdateCounts();
    }


}

