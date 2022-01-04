import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Database_Setup {
	static Connection DatabaseConnection = null;
	static String ValueFromDB;
	public static void main(String[] args) {
		//Connection to Heroku-based Postgresql database server
         CreateDB_and_MD5Table();
         Insertion();
         View();
	}
	 public static void CreateDB_and_MD5Table() {
         String createSQLTable = null;
         Statement StatementConnect = null;
         try {
        	//Here I'm importing connection from a connection method inside keys.java class(git ignored) in order to hide database credentials
            DatabaseConnection  = Keys.DatabaseConnection();
 			System.out.println("Database Connected ..");
 			StatementConnect = DatabaseConnection.createStatement();
 			createSQLTable = "CREATE TABLE VirusMD5(MD5 VARCHAR(50) PRIMARY KEY)";
 			/*check if table "VirusMD5" exists */
 			DatabaseMetaData dbm = DatabaseConnection.getMetaData();
 			ResultSet tables = dbm.getTables(null, null, "VirusMD5", null);
	 		if (!tables.next()) { // If table does not exist
	 			StatementConnect.executeUpdate(createSQLTable);
	 	        System.out.println("Table \"VirusMD5\" created");	
	 		}
 		} catch (SQLException e) {
 			System.out.println("Table \"VirusMD5\" already exists");
 		}    	 
     }
	 /*Transferring data obtained from virusshare.com as a text file into my database
	  * I am limiting the data storage to 10,000 rows as per Heroku's free plan limit
	  * by using the first 10k rows from the 64k data
	  */
	public static void Insertion() {
		 try { 
			 File file_obj = new File("C:\\Users\\eliya\\eclipse-workspace\\Experimental\\src\\Viruses_MD5_Data.txt");
				 Scanner sc  = new Scanner(file_obj);
				 PreparedStatement StatementInsert = DatabaseConnection.prepareStatement("insert into VirusMD5(MD5) values(?)");
				 //the following 1 statement inserts MD5 of the virus file "DangerousVirus_DoNOTRun.bat" 
				 Statement Insert  = DatabaseConnection.createStatement();
				 Insert.executeUpdate("insert into VirusMD5(MD5) values('8750b731694012ab9cfdb096296fb8e8')");
				 
				 int i=1;
				 while(sc.hasNextLine() && i<=9999) {
					 String line = sc.nextLine();					
					 StatementInsert.setString(1, line);
					 StatementInsert.executeUpdate();
					 i++;
				 }
				 StatementInsert.close();
		} catch (org.postgresql.util.PSQLException e) {
			System.out.println("duplicate key value violates unique constraint \"virusmd5_pkey\"");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("Make sure file exists");
		} 
	 }
	//view the data stored 
	 public static void View() {
		 try {
			 Statement StatementSelect = DatabaseConnection.createStatement();
			 ResultSet DataResult; //create a return data object
			 DataResult = StatementSelect.executeQuery("SELECT * from VirusMD5");
			 while (DataResult.next() ) {
				ValueFromDB = DataResult.getString("MD5");
	            System.out.println(ValueFromDB);
			 }
	    } catch (Exception e) {
	        System.out.println("please try again! ");
	    }
	 }
}
