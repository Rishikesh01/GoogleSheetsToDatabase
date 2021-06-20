package repository;

import driver.DriverConnector;

import java.sql.*;
import java.util.ArrayList;
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

    public void queryPrinter(String sql) {
        try {
            /*
            formula for padding:
            highest char length in column - current column data char length +2
             */

            connection = connector.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();
            int[] maxColWidth = new int[columnCount];
            String[] colNames = new String[columnCount];
            List<List<String>> rows = new ArrayList<>(20);

             /*
             get column names in array then
             add the length of the column name in maxColWidth array to have minimum value
              */
            for (int i = 1; i <= columnCount; i++) {
                colNames[i - 1] = metaData.getColumnLabel(i);
                maxColWidth[i - 1] = colNames[i - 1].length();
            }

            //loop though rows and to get max length of chars in column and place them in maxColWidth array
            while (rs.next()) {
                List<String> list = new ArrayList<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    maxColWidth[i - 1] = Math.max(maxColWidth[i - 1], rs.getString(i).length());
                    list.add(rs.getString(i));

                }
                rows.add(list);
            }
            //print column names with appropriate padding
            for (int i = 0; i < columnCount; i++) {
                String padding = new String(
                        new char[maxColWidth[i] - colNames[i].length() + 2]
                ).replace("\0", " ");
                System.out.print(colNames[i] + padding);
            }
            System.out.println();

            //print column data with appropriate padding
            for (List<String> ls : rows) {
                for (int i = 0; i < maxColWidth.length; i++) {
                    String padding = new String(
                            new char[maxColWidth[i] - ls.get(i).length() + 2]
                    ).replace("\0", " ");
                    System.out.print(ls.get(i) + padding);
                }
                System.out.println();

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
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

    public void insertData(List<List<Object>> values,String rowNames) {
    }

    public int getPrimaryKey(String tableName) {
        try {
            connection = connector.getConnection();
            DatabaseMetaData dbData = null;
            dbData = connection.getMetaData();
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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
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

