package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

public class DatabaseDAO {

	private static DatabaseDAO instance;
	private Connection c;
	private String databasePath;
	
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
	
	public void createDatabase() {
		Statement stmt = null;
		
		try {
		
			connect();
			
			System.out.println("Opened database successfully. Let's add tables");

			
			String user = "CREATE TABLE USER " +
					"(USERNAME		TEXT		NOT NULL, " +
					" ACCESS_TOKEN	TEXT		NOT NULL, " + 
					" ACCESS_SECRET	TEXT		NOT NULL, " +
					" PRIMARY KEY	(USERNAME))"; 

			String collection = "CREATE TABLE COLLECTION " +
					"(COLLECTION_ID		INTEGER			PRIMARY KEY		AUTOINCREMENT, " +
					" USERNAME			TEXT			NOT NULL, " +
					" TIME_START		TIMESTAMP		NOT NULL, " + 
					" TIME_END			TIMESTAMP		NOT NULL, " +
					" TYPE				VARCHAR(50)		NOT NULL, " +
					" QUERY				VARCHAR(50)		NOT NULL, " +
					" FOREIGN KEY 		(USERNAME)	REFERENCES	USER(USERNAME))";

			String tweet = "CREATE TABLE TWEET " +
					"(TWEET_ID			INTEGER			NOT NULL, " +
					" COLLECTION_ID		VARCHAR(50)		NOT NULL, " + 
//					" RAW_TWEET			VARCHAR(200)	NOT NULL, " + // poner luego !! 
					" AUTHOR			VARCHAR(50)		NOT NULL, " +
					" CREATED_AT		VARCHAR(50)		NOT NULL, " +
					" TEXT_PRINTABLE	VARCHAR(200)	NOT NULL, " +
					" CITY				VARCHAR(50), " +
					" COUNTRY			VARCHAR(50), " +
					" PRIMARY KEY		(TWEET_ID), "+
					" FOREIGN KEY 		(COLLECTION_ID)	REFERENCES COLLECTION(COLLECTION_ID))";

			
			stmt = c.createStatement();
			stmt.executeUpdate(user);
			stmt.executeUpdate(collection);
			stmt.executeUpdate(tweet);
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		} finally {
			try {
				stmt.close();
				System.out.println("Initial Statement closed?"+stmt.isClosed());
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Database created successfully");
	}
	
	public Connection getConnection() {
		return c;
	}
	
}	
/*	
	public Database(String path) {
		databasePath = path;
	}

	*//**
	 * Connect to a database
	 * @param path
	 *//*
	public static void connect() {
		
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	*//**
	 * Create our twitter searcher database
	 *//*
	public static void createDatabase(String path) {

		Statement stmt = null;
		databasePath = path;
		
		try {
		
			connect();
			
			System.out.println("Opened database successfully. Let's add tables");

			
			String user = "CREATE TABLE USER " +
					"(USERNAME		TEXT		NOT NULL, " +
					" ACCESS_TOKEN	TEXT		NOT NULL, " + 
					" ACCESS_SECRET	TEXT		NOT NULL, " +
					" PRIMARY KEY	(USERNAME))"; 

			String collection = "CREATE TABLE COLLECTION " +
					"(COLLECTION_ID		INTEGER			PRIMARY KEY		AUTOINCREMENT, " +
					" USERNAME			TEXT			NOT NULL, " +
					" TIME_START		TIMESTAMP		NOT NULL, " + 
					" TIME_END			TIMESTAMP		NOT NULL, " +
					" TYPE				VARCHAR(50)		NOT NULL, " +
					" QUERY				VARCHAR(50)		NOT NULL, " +
					" FOREIGN KEY 		(USERNAME)	REFERENCES	USER(USERNAME))";

			String tweet = "CREATE TABLE TWEET " +
					"(TWEET_ID			INTEGER			NOT NULL, " +
					" COLLECTION_ID		VARCHAR(50)		NOT NULL, " + 
//					" RAW_TWEET			VARCHAR(200)	NOT NULL, " + // poner luego !! 
					" AUTHOR			VARCHAR(50)		NOT NULL, " +
					" CREATED_AT		VARCHAR(50)		NOT NULL, " +
					" TEXT_PRINTABLE	VARCHAR(200)	NOT NULL, " +
					" CITY				VARCHAR(50), " +
					" COUNTRY			VARCHAR(50), " +
					" PRIMARY KEY		(TWEET_ID), "+
					" FOREIGN KEY 		(COLLECTION_ID)	REFERENCES COLLECTION(COLLECTION_ID))";

			
			stmt = c.createStatement();
			stmt.executeUpdate(user);
			stmt.executeUpdate(collection);
			stmt.executeUpdate(tweet);
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		} finally {
			try {
				stmt.close();
				System.out.println("Initial Statement closed?"+stmt.isClosed());
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Database created successfully");
	}
	
	public static Connection getConnection() {
		return c;
	}

}
*/