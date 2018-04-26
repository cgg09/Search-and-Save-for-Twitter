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
			"(collection_id		INTEGER			PRIMARY KEY		AUTOINCREMENT, " +
			" username			TEXT			NOT NULL, " +
			" time_start		TEXT			NOT NULL, " + 
			" time_end			TEXT			NOT NULL, " +
			" type				VARCHAR(50)		NOT NULL, " +
			" query				VARCHAR(50)		NOT NULL, " +
			" FOREIGN KEY 		(USERNAME)	REFERENCES	USER(USERNAME))";
	
	private String tweetTable = "CREATE TABLE tweet " +
			"(tweet_id			INTEGER			NOT NULL, " +
			" collection_id		TEXT			NOT NULL, " + 
			" raw_tweet			TEXT			NOT NULL, " + 
			" author			VARCHAR(50)		NOT NULL, " +
			" created_at		TEXT			NOT NULL, " +
			" text_printable	VARCHAR(200)	NOT NULL, " +	// FIXME pendiente de parsear texto !!
			" retweet			INTEGER			NOT NULL, " +
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

	public Connection getConnection() {
		return c;
	}
	
	public void connect() {
		try {
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
