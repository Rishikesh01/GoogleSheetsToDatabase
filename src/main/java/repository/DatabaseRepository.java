package repository;

import driver.DriverConnector;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

/*
1.user input will data with datatype writes query create table();
2.I need to store record in database with (columns);
3.separate tables with there year;
4.
 */
public class DatabaseRepository {
    private Connection connection;
    private final DriverConnector connector;

    public DatabaseRepository(DriverConnector connector) {

        this.connector = connector;
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
    }

    public Boolean createTable() {
        connection = connector.getConnection();
        System.out.println("Enter the Query to Create Table");
        Scanner sc = new Scanner(System.in);
        String query = sc.nextLine();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Check Syntax");
            return false;
        }
        System.out.println("Table Created in DataBase");
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public void insertData(List<List<Object>> list) {
    }

    public int getPrimaryKey(String tableName) throws SQLException {
        connection = connector.getConnection();
        DatabaseMetaData dbData = connection.getMetaData();
        ResultSet rs = dbData.getPrimaryKeys(null, null, tableName);
        String primaryKey = "";
        while (rs.next())
            primaryKey = rs.getString(4);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from " + tableName);
        rs = preparedStatement.executeQuery();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            if (rs.getMetaData().getColumnLabel(i).equals(primaryKey))
                return i;//column number in row
        }
        return 0; //means not found
    }

    public String[] getColDataType(String tableName) {
        try {
            connector.getConnection();
            Statement stmt = connection.createStatement();
            /*
            select all columns form table
            and iterate through all the columns name and save in array
             */
            ResultSet rs = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rm = rs.getMetaData();
            int colCount = rm.getColumnCount();
            String[] columnType = new String[colCount];
            for (int i = 1; i <= colCount; i++)
                columnType[i - 1] = rm.getColumnClassName(i);
            return columnType;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

