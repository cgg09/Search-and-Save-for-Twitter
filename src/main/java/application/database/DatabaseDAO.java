package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;

/**
 * DAO class to manage the creation and maintenance of the database
 * @author Maria Cristina, github: cgg09
 *
 */

public class DatabaseDAO {

	private static DatabaseDAO instance;
	private Connection c;
	private SQLiteConfig config;
	private String databasePath;
	
	//Queries
	private String checkU = "SELECT name FROM sqlite_master WHERE type='table' AND name='user'";
	private String checkC = "SELECT name FROM sqlite_master WHERE type='table' AND name='collection'";
	private String checkT = "SELECT name FROM sqlite_master WHERE type='table' AND name='tweet'";
	
	private String userTable = "CREATE TABLE user (" +
			"    username      TEXT PRIMARY KEY NOT NULL," +
			"    access_token  TEXT NOT NULL," +
			"    access_secret TEXT NOT NULL" +
			");";
	
	private String collectionTable = "CREATE TABLE collection (" +
			"    collection_id INTEGER      PRIMARY KEY AUTOINCREMENT NOT NULL," +
			"    username      TEXT         NOT NULL REFERENCES user (username) ON DELETE CASCADE," +
			"    time_start    TEXT         NOT NULL," +
			"    time_end      TEXT," +
			"    type          VARCHAR (50) NOT NULL," +
			"    query         VARCHAR (50) NOT NULL" +
			");";

	private String tweetTable = "CREATE TABLE tweet (" +
			"    tweet_id       INTEGER       PRIMARY KEY NOT NULL," +
			"    collection_id  INTEGER       NOT NULL REFERENCES collection (collection_id) ON DELETE CASCADE," +
			"    author         VARCHAR (50)  NOT NULL," +
			"    created_at     TEXT          NOT NULL," +
			"    text_printable VARCHAR (200) NOT NULL," +
			"    retweet        INTEGER       NOT NULL," +
			"    raw_tweet      TEXT          NOT NULL" +
			");";
	
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
		config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+databasePath,config.toProperties());
		} catch ( Exception e ) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * Check if the database is created correctly
	 * @throws DatabaseReadException
	 */
	public void checkDatabase() throws DatabaseReadException { //FIXME it does not work correctly, it does not detect errors in the tables
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
			//System.out.println("Hi, users");
			try {
				createUserTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}
		
		if(psc == null) {
			//System.out.println("Hi, collections");
			try {
				createCollectionTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}
		
		if(pst == null) {
			//System.out.println("Hi, tweets");
			try {
				createTweetTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}
	
		System.out.println("DB checked");
	}
	
	/**
	 * Creates table to save each user information
	 * @throws DatabaseWriteException
	 */
	public void createUserTable() throws DatabaseWriteException {
		Statement stmtU = null;
		try {
			stmtU = c.createStatement();
			stmtU.executeUpdate(userTable);
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error creating the users table.",e);
		}
	}
	
	/**
	 * Creates table to save each search information
	 * @throws DatabaseWriteException
	 */
	public void createCollectionTable() throws DatabaseWriteException {
		Statement stmtC = null;
		try {
			stmtC = c.createStatement();
			stmtC.executeUpdate(collectionTable);
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error creating the collections table.",e);
		}
	}
	
	/**
	 * Creates table to save each tweet information
	 * @throws DatabaseWriteException
	 */
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
