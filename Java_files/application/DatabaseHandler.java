package application;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.mysql.jdbc.Driver;

import javafx.scene.control.Alert;

public class DatabaseHandler {
	
	private static DatabaseHandler Handler;
	
	private static final String DUrl = "jdbc:mysql://localhost:3306/library_management?"
											+ "createDatabaseIfNotExist=true&autoReconnect=true&useSSL=false";
	private static Connection conn = null;
	private static Statement stmt = null;
	
	private DatabaseHandler() {
		createConnection();
		setUpBookTable();
		setUpMemberTable();
		setUpBookIssueTable();
	}

	public static DatabaseHandler getInstance(){
		if(Handler == null){
			synchronized(DatabaseHandler.class){
				if(Handler == null)
					Handler = new DatabaseHandler();
			}
		}
			
		return Handler;
	}
	
	private void createConnection() {
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			Driver myDriver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(myDriver);
			conn = DriverManager.getConnection(DUrl,"root","pranjul");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void setUpBookTable() {
		String Table_name = "BOOKSHELF";
		try {
			stmt = conn.createStatement();
			DatabaseMetaData dataMeta = conn.getMetaData();
			ResultSet set = dataMeta.getTables(null, null, Table_name.toUpperCase(), null);
			
			if(set.next()){
				System.out.println(Table_name + " Already Exist ! GO !");
			}else{
				String tableSql = "create table BOOKSHELF("
						+ "id varchar(200) primary key,"
						+ "title varchar(200) ,"
						+ "author varchar(200) ,"
						+ "publisher varchar(200) ,"
						+ "isAvail Boolean default true)";
				stmt.execute(tableSql);
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage() + " ....Setup Database");
		}
	}

	private void setUpMemberTable() {
		String Table_name = "LIBRARY_MEMBERS";
		try {
			stmt = conn.createStatement();
			DatabaseMetaData dataMeta = conn.getMetaData();
			ResultSet set = dataMeta.getTables(null, null, Table_name.toUpperCase(), null);
			
			if(set.next()){
				System.out.println(Table_name + " Already Exist ! GO !");
			}else{
				String tableSql = "create table LIBRARY_MEMBERS("
						+ "id varchar(20) primary key,"
						+ "name varchar(45),"
						+ "mobile varchar(20),"
						+ "email varchar(45))";
				stmt.execute(tableSql);
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage() + " ....Setup Database");
		}
	}
	
	private void setUpBookIssueTable() {
		String Table_name = "BOOKISSUE";
		try {
			stmt = conn.createStatement();
			DatabaseMetaData dataMeta = conn.getMetaData();
			ResultSet set = dataMeta.getTables(null, null, Table_name.toUpperCase(), null);
			
			if(set.next()){
				System.out.println(Table_name + " Already Exist ! GO !");
			}else{
				String tableSql = "create table BOOKISSUE("
						+ "bookId varchar(45) primary key,"
						+ "memberId varchar(45) ,"
						+ "issueTime timestamp default CURRENT_TIMESTAMP,"
						+ "renew_count integer default 0,"
						+ "FOREIGN KEY (bookId) REFERENCES BOOKSHELF(id),"
						+ "FOREIGN KEY (memberId) REFERENCES LIBRARY_MEMBERS(id))";
				stmt.execute(tableSql);
				
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage() + " ....Setup Database");
		}
	}
	
	
	public boolean executeAction(String sql) {
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			return true;
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
		return false;
	}

	public ResultSet executeQuery(String sql) {
		ResultSet result = null;
		try {
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);
			return result;
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
		return result;
	}
	
}
