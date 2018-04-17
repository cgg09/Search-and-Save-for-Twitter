package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseDAO {

	private static DatabaseDAO instance;
	private Connection c;
	private String databasePath;
	
	//Queries
	private String checkU = "SELECT name FROM sqlite_master WHERE type='table' AND name='user'";
	private String checkC = "SELECT name FROM sqlite_master WHERE type='table' AND name='collection'";
	private String checkT = "SELECT name FROM sqlite_master WHERE type='table' AND name='tweet'";
	
	private String userTable = "CREATE TABLE user " +
			"(username TEXT not NULL, " +
			" access_token TEXT not NULL, " + 
			" access_secret TEXT not NULL, " +
			" PRIMARY KEY ( username ))";
	
	private String collectionTable = "CREATE TABLE COLLECTION " +
			"(COLLECTION_ID		INTEGER			PRIMARY KEY		AUTOINCREMENT, " +
			" USERNAME			TEXT			NOT NULL, " +
			" TIME_START		TEXT			NOT NULL, " + 
			" TIME_END			TEXT			NOT NULL, " +
			" TYPE				VARCHAR(50)		NOT NULL, " +
			" QUERY				VARCHAR(50)		NOT NULL, " +
			" FOREIGN KEY 		(USERNAME)	REFERENCES	USER(USERNAME))";
	
	private String tweetTable = "CREATE TABLE TWEET " +
			"(TWEET_ID			INTEGER			NOT NULL, " +
			" COLLECTION_ID		TEXT			NOT NULL, " + 
			" RAW_TWEET			TEXT			NOT NULL, " + 
			" AUTHOR			VARCHAR(50)		NOT NULL, " +
			" CREATED_AT		TEXT			NOT NULL, " +
			" TEXT_PRINTABLE	VARCHAR(200)	NOT NULL, " +	// FIXME pendiente de parsear texto !!
			" PRIMARY KEY		(TWEET_ID), "+
			" FOREIGN KEY 		(COLLECTION_ID)	REFERENCES COLLECTION(COLLECTION_ID))";
	
	
	private DatabaseDAO(String path) {
		databasePath = path;
	}
	
	public static DatabaseDAO getInstance(String path) {
		if (instance == null) {
			instance = new DatabaseDAO(path);
		}
		return instance;
	}
	
	public void connect() {
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public void checkDatabase() {
		connect();
		
		PreparedStatement psu = null;
		PreparedStatement psc = null;
		PreparedStatement pst = null;
		
		try {
			psu = c.prepareStatement(checkU);
			psc = c.prepareStatement(checkC);
			pst = c.prepareStatement(checkT);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(psu == null) {
			System.out.println("Hi, users");
			createUserTable();
		}
		
		if(psc == null) {
			System.out.println("Hi, collections");
			createCollectionTable();
		}
		
		if(pst == null) {
			System.out.println("Hi, tweets");
			createTweetTable();
		}
	
		System.out.println("DB checked");
	}
	
	public void createUserTable() {
		Statement stmtU = null;
		try {
			stmtU = c.createStatement();
			stmtU.executeUpdate(userTable);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createCollectionTable() {
		Statement stmtC = null;
		try {
			stmtC = c.createStatement();
			stmtC.executeUpdate(collectionTable);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createTweetTable() {
		Statement stmtT = null;
		try {
			stmtT = c.createStatement();
			stmtT.executeUpdate(tweetTable);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return c;
	}
	
}	
