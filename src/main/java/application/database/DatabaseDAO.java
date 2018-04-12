package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
					" TIME_START		TEXT			NOT NULL, " + 
					" TIME_END			TEXT			NOT NULL, " +
					" TYPE				VARCHAR(50)		NOT NULL, " +
					" QUERY				VARCHAR(50)		NOT NULL, " +
					" FOREIGN KEY 		(USERNAME)	REFERENCES	USER(USERNAME))";

			String tweet = "CREATE TABLE TWEET " +
					"(TWEET_ID			INTEGER			NOT NULL, " +
					" COLLECTION_ID		TEXT			NOT NULL, " + 
					" RAW_TWEET			TEXT			NOT NULL, " + // poner luego !! 
					" AUTHOR			VARCHAR(50)		NOT NULL, " +
					" CREATED_AT		TEXT			NOT NULL, " +
					" TEXT_PRINTABLE	VARCHAR(200)	NOT NULL, " +
					" PRIMARY KEY		(TWEET_ID), "+
					" FOREIGN KEY 		(COLLECTION_ID)	REFERENCES COLLECTION(COLLECTION_ID))";
			
			stmt = c.createStatement();
			stmt.executeUpdate(user);
			stmt.executeUpdate(collection);
			stmt.executeUpdate(tweet);
		
			System.out.println("Database created successfully");
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			//FIXME popup para usuario
		}
	
	}
	
	public Connection getConnection() {
		return c;
	}
	
}	
