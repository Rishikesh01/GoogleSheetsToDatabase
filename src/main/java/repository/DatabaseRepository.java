package repository;

import driver.DriverConnector;

import java.sql.*;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Date;
/*
1.user input will data with datatype writes query create table();
2.I need to store record in database with (columns);
3.separate tables with there year;
4.
 */

public class DatabaseRepository {
    private Connection connection;
    private final DriverConnector connector;

    public void setColumnNamesStr(String columnNamesStr) {
        this.columnNamesStr = columnNamesStr;
    }

    private String columnNamesStr;
    private long[] executeLargeBatch;

    public DatabaseRepository(DriverConnector connector) {

        this.connector = connector;
    }

    public void queryPrinter(String sql) {
        try {
            /*
             * formula for padding: highest char length in column - current column data char
             * length +2
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
             * get column names in array then add the length of the column name in
             * maxColWidth array to have minimum value
             */
            for (int i = 1; i <= columnCount; i++) {
                colNames[i - 1] = metaData.getColumnLabel(i);
                maxColWidth[i - 1] = colNames[i - 1].length();
            }

            // loop though rows and to get max length of chars in column and place them in
            // maxColWidth array
            while (rs.next()) {
                List<String> list = new ArrayList<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    maxColWidth[i - 1] = Math.max(maxColWidth[i - 1], rs.getString(i).length());
                    list.add(rs.getString(i));

                }
                rows.add(list);
            }
            // print column names with appropriate padding
            for (int i = 0; i < columnCount; i++) {
                String padding = new String(new char[maxColWidth[i] - colNames[i].length() + 2]).replace("\0", " ");
                System.out.print(colNames[i] + padding);
            }
            System.out.println();

            // print column data with appropriate padding
            for (List<String> ls : rows) {
                for (int i = 0; i < maxColWidth.length; i++) {
                    String padding = new String(new char[maxColWidth[i] - ls.get(i).length() + 2]).replace("\0", " ");
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

    public  synchronized void insertData(final List<List<Object>> values) throws Exception {
        connection=connector.getConnection();
        int i;
        String query="INSERT INTO USER VALUES(";//works
        //or insert into User(column names) values(data);
        StringBuilder mquery=new StringBuilder(query);
        int colNo=5;//values.size; colNo=List.getMust give Number of Colomns
        for(i=1;i<=colNo;i++)
        {
            if(i==colNo)
                mquery.append("?);");
            else
            mquery.append("?,");
        }
        String tableName="user";
        System.out.println(mquery);
        PreparedStatement pStatement = connection.prepareStatement
        (String.valueOf(mquery));
        connection.setAutoCommit(false);
        for(List<Object>list:values)
        {
            for(i=1;i<=list.size();i++)
            {
                
                String []datatype=getColDataType(tableName);
                /*
                Java Type 	JDBC type
                String 	VARCHAR or LONGVARCHAR
                java.math.BigDecimal 	NUMERIC
                boolean 	BIT
                Int 	TINYINT
                int 	SMALLINT
                int 	INTEGER
                long 	BIGINT
                float 	REAL
                double 	DOUBLE

                java.sql.Date 	DATE
                java.sql.Time 	TIME
                java.sql.Timestamp 	TIMESTAMP 
                */
                
                //integer family
                //Applicable to Tiny Int,Small Int,Integer,BigDecimal
                if(datatype[i].substring(10).equals("Integer")
                    pStatement.setInt(i,(int) list.get(i));
                if(datatype[i].substring(10).equals("Long")
                    pStatement.setLong(i, (long) list.get(i));
                else if(datatype[i].substring(10).equals("BigDecimal"))
                    pStatement.setBigDecimal(i, (BigDecimal)list.get(i))
                //String Family varchar,Long varchar
                else if(datatype[i].substring(10).equals("String"))
                    pStatement.setString(i,(String)list.get(i));
                //Byte family T/F
                else if(datatype[i].substring(10).equals("Boolean"))
                    pStatement.setBoolean(i, (boolean) list.get(i));   
                //Float or Real or Double 
                else if(datatype[i].substring(10).equals("Real"))
                    pStatement.setFloat(i,(Float)list.get(i));
                else if(datatype[i].substring(10).equals("Float"))
                    pStatement.setFloat(i,(Float)list.get(i));
                else if(datatype[i].substring(10).equals("Double"))
                    pStatement.setDouble(i,(Double)list.get(i));
                //Date
                else if(datatype[i].substring(10).equals("Date"))
                    pStatement.setDate(i, (java.sql.Date) list.get(i));
                //Time
                else if(datatype[i].substring(10).equals("Time"))
                    pStatement.setTime(i, (java.sql.Time) list.get(i));
                //Timestamp
                else if(datatype[i].substring(10).equals("Timestamp"))
                    pStatement.setTimestamp(i, (Timestamp) list.get(i));
            
            }
            pStatement.addBatch();
        }
        long []Bt = pStatement.executeLargeBatch();
        /* To check all query Exceuted Succssfully
            0-> unsuccessfull
            1-> succesfull
        */
        connection.commit();
        connection.setAutoCommit(true);
        System.out.println("Susscessfully Inserted in Database\n");    
    }

    public int getPrimaryKey(String tableName) {
        try {
            connection = connector.getConnection();
            DatabaseMetaData dbData;
            dbData = connection.getMetaData();
            ResultSet rs = dbData.getPrimaryKeys(null, null, tableName);
            String primaryKey = "";

            while (rs.next())
                primaryKey = rs.getString(4);

            PreparedStatement preparedStatement = connection.prepareStatement("select * from " + tableName);
            rs = preparedStatement.executeQuery();

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                if (rs.getMetaData().getColumnLabel(i).equals(primaryKey))
                    return i;// column number in row
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
        return 0; // means not found
    }

    public String[] getColDataType(String tableName) {
        try {
            connector.getConnection();
            Statement stmt = connection.createStatement();
            /*
             * select all columns form table and iterate through all the columns name and
             * save in array
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