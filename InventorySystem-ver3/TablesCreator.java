//STEP 1. Import required packages
import java.sql.*;

public class TablesCreator {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/inventoryDB";

   //  Database credentials
   static final String USER = "tkly";
   static final String PASS = "tkly";
   
   public static void main(String[] args) {
   Connection conn = null;
   Statement stmtBook = null;
   Statement stmtCd = null;
   Statement stmtDvd = null;

   try{
      // Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      // Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");
      
      // Execute queries to create tables for each inventory type
      System.out.println("Creating table in given database...");
      
      stmtBook = conn.createStatement();
      String createBookDBQuery =
 			  " CREATE TABLE BookData " +
              " ( ProductID VARCHAR(255) not NULL, " +
              " Title VARCHAR(255), " + 
              " Author VARCHAR(255), " + 
              " Year INTEGER, " +
              " Quantity INTEGER, " +
              " Price INTEGER, " +
              " PRIMARY KEY ( ProductId ) )"; 

      
      stmtCd = conn.createStatement();
      String createCdDBQuery =
    		  " CREATE TABLE CdData " +
              " ( ProductID VARCHAR(255) not NULL, " +
              " Title VARCHAR(255), " + 
              " Artist VARCHAR(255), " + 
              " Year INTEGER, " +
              " Quantity INTEGER, " +
              " Price INTEGER, " +
              " PRIMARY KEY ( ProductId ) )";
     
      stmtDvd = conn.createStatement();
      String createDvdDBQuery =
    		  " CREATE TABLE DvdData " +
              " ( ProductID VARCHAR(255) not NULL, " +
              " Title VARCHAR(255), " + 
              " Director VARCHAR(255), " + 
              " Year INTEGER, " +
              " Quantity INTEGER, " +
              " Price INTEGER, " +
              " PRIMARY KEY ( ProductId ) )";
      
      stmtBook.executeUpdate(" DROP TABLE IF EXISTS BookData ");
      stmtBook.executeUpdate(createBookDBQuery);
      
      stmtCd.executeUpdate("DROP TABLE IF EXISTS CdData ");
      stmtCd.executeUpdate(createCdDBQuery);
      
      stmtCd.executeUpdate("DROP TABLE IF EXISTS DvdData ");
      stmtDvd.executeUpdate(createDvdDBQuery);
      
      System.out.println("Created table in given database...");
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmtBook!=null)
            conn.close();
      }catch(SQLException se){
      }// do nothing
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}//end main
}//end JDBCExample