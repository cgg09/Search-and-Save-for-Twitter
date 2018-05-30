package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;

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
	
	private String collectionTable = "CREATE TABLE collection " +
			"(collection_id INTEGER PRIMARY KEY AUTOINCREMENT not NULL, " +
			" username TEXT	not NULL, " +
			" time_start TEXT not NULL, " + 
			" time_end TEXT, " +
			" type VARCHAR(50) not NULL, " +
			" query	VARCHAR(50) not NULL, " +
			" FOREIGN KEY (username) REFERENCES	user(username) ON DELETE CASCADE)";
	
	private String tweetTable = "CREATE TABLE tweet " +
			"(tweet_id INTEGER not NULL, " +
			" collection_id INTEGER not NULL, " +  
			" author VARCHAR(50) not NULL, " +
			" created_at TEXT not NULL, " +
			" text_printable VARCHAR(200) not NULL, " +
			" retweet INTEGER not NULL, " +
			" raw_tweet	TEXT not NULL, " +
			" PRIMARY KEY (tweet_id, collection_id), "+
			" FOREIGN KEY (collection_id) REFERENCES collection(collection_id) ON DELETE CASADE)";
	
	
	private DatabaseDAO(String path) {
		databasePath = path;
	}
	
	public static DatabaseDAO getInstance(String path) {
		if (instance == null) {
			instance = new DatabaseDAO(path);
		}
		return instance;
	}

	public Connection getConnection() {
		return c;
	}
	
	public void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public void checkDatabase() throws DatabaseReadException {
		connect();
		
		PreparedStatement psu = null;
		PreparedStatement psc = null;
		PreparedStatement pst = null;
		
		try {
			psu = c.prepareStatement(checkU);
			psc = c.prepareStatement(checkC);
			pst = c.prepareStatement(checkT);
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error reading the database.",e);
		}
		
		if(psu == null) {
			System.out.println("Hi, users");
			try {
				createUserTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}
		
		if(psc == null) {
			System.out.println("Hi, collections");
			try {
				createCollectionTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}
		
		if(pst == null) {
			System.out.println("Hi, tweets");
			try {
				createTweetTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}
	
		System.out.println("DB checked");
	}
	
	public void createUserTable() throws DatabaseWriteException {
		Statement stmtU = null;
		try {
			stmtU = c.createStatement();
			stmtU.executeUpdate(userTable);
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error creating the users table.",e);
		}
	}
	
	public void createCollectionTable() throws DatabaseWriteException {
		Statement stmtC = null;
		try {
			stmtC = c.createStatement();
			stmtC.executeUpdate(collectionTable);
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error creating the collections table.",e);
		}
	}
	
	public void createTweetTable() throws DatabaseWriteException {
		Statement stmtT = null;
		try {
			stmtT = c.createStatement();
			stmtT.executeUpdate(tweetTable);
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error creating the tweets table.",e);
		}
	}
	
}	
