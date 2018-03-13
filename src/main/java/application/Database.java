package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;


public class Database {

	private static Connection c = null;
	private static String className = "org.sqlite.JDBC";
	private static String databasePath;

	/**
	 * Connect to a sample database
	 */
	public static void connect(String path) {

		databasePath = path;
		try {
			Class.forName(className);
			c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		System.out.println("Opened database successfully");

	}

	/**
	 * Create our twitter searcher database
	 */
	public static void createDatabase(String path) {

		databasePath = path;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			SQLiteConfig config = new SQLiteConfig();
			config.enforceForeignKeys(true);
			c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			System.out.println("Opened database successfully. Let's add tables");

			stmt = c.createStatement();
			String user = "CREATE TABLE USER " +
					"(USERNAME		TEXT	PRIMARY KEY		NOT NULL, " +
					" ACCESS_TOKEN	TEXT					NOT NULL, " + 
					" ACCESS_SECRET	TEXT					NOT NULL)"; 

			String collection = "CREATE TABLE COLLECTION " +
					"(COLLECTION_ID		CHAR(50)	PRIMARY KEY		NOT NULL, " +
					" USERNAME			TEXT						NOT NULL, " +  // REVISAR COMO INDICAR FKS
					" TIME_START		TIMESTAMP					NOT NULL, " + 
					" TIME_END			TIMESTAMP					NOT NULL, " +
					" TYPE				CHAR(50)					NOT NULL, " +
					" QUERY				CHAR(50)					NOT NULL, " +
					" FOREIGN KEY 		(USERNAME)	REFERENCES		USER(USERNAME))";

			String tweet = "CREATE TABLE TWEET " +
					"(TWEET_ID			CHAR(50)		PRIMARY KEY     NOT NULL, " +
					" COLLECTION_ID		CHAR(50)						NOT NULL, " + 
					" RAW_TWEET			CHAR(200)						NOT NULL, " + 
					" AUTHOR			CHAR(50)						NOT NULL, " +
					" CREATED_AT		TIMESTAMP						NOT NULL, " +
					" TEXT_PRINTABLE	CHAR(200)						NOT NULL, " +
					" FOREIGN KEY 		(COLLECTION_ID)	REFERENCES		COLLECTION(COLLECTION_ID))";

			stmt.executeUpdate(user);
			stmt.executeUpdate(collection);
			stmt.executeUpdate(tweet);
			stmt.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		System.out.println("Tables created successfully");
	}
	
	/**
	 * Saves the login info of the user in the database
	 * @param username
	 * @param token
	 * @param tokenSecret
	 */
	public static void saveLogin(String username, String token, String tokenSecret) {	

		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			
			String loginn = "INSERT INTO USER (USERNAME, ACCESS_TOKEN, ACCESS_SECRET) " +
					"VALUES (?,?,?);";
			
			PreparedStatement psmt = c.prepareStatement(loginn);
			
			psmt.setString(1, username);
			psmt.setString(2, token);
			psmt.setString(3, tokenSecret);
			psmt.executeUpdate();
		
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public static boolean checkUser(String user) {
		
		Statement stmt = null;

		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			stmt = c.createStatement();
			
			String s = "SELECT username FROM user WHERE username=\""+user+"\" ";
			ResultSet rs = stmt.executeQuery(s);
			
			if(rs!=null) {
				return true;
			} else {
				return true;
			}
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
	}
	
	public static String getUserData(String query, String user) {
		
		Statement stmt = null;

		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			stmt = c.createStatement();

			String select = "SELECT "+query+" FROM user WHERE username=\""+user+"\" ";
			
			ResultSet rs = stmt.executeQuery(select);
			
			return rs.toString();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
		
	}


}
