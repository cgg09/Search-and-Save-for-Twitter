package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.sqlite.SQLiteConfig;

import application.model.HistoricSearch;
import application.model.LiveSearch;
import application.model.TwitterSearch;
import twitter4j.Status;


public class Database {

	private static Connection c = null;
	private static String className = "org.sqlite.JDBC";
	private static String databasePath;
	private static String user;

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

		user = username;
		
		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			
			String login = "INSERT INTO USER (USERNAME, ACCESS_TOKEN, ACCESS_SECRET) " +
					"VALUES (?,?,?);";
			
			PreparedStatement psmt = c.prepareStatement(login);
			
			psmt.setString(1, username);
			psmt.setString(2, token);
			psmt.setString(3, tokenSecret);
			psmt.executeUpdate();
			psmt.close();
		
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	/**
	 * Check if the user exists in the database to do the fast login
	 * @param user
	 * @return
	 */
	public static boolean checkUser(String username) {
		
		Statement stmt = null;

		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			stmt = c.createStatement();
			
			String s = "SELECT username FROM user WHERE username=\""+username+"\" ";
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
	
	/**
	 * Get user data
	 * @param query
	 * @param user
	 * @return
	 */
	public static String getUserData(String query, String username) {
		
		user = username;
		
		Statement stmt = null;

		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			stmt = c.createStatement();

			String select = "SELECT "+query+" FROM user WHERE username=\""+user+"\" ";

			ResultSet rs = stmt.executeQuery(select);
			
			while (rs.next()) {
                return rs.getString(query);
            }
			
			return null;

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
		
	}
	
	/**
	 * Add a new search to the database
	 * @param search
	 * @param user
	 */
	public static void addNewCollection(TwitterSearch search, Timestamp start, Timestamp end) {
		
		String type = "";
		
		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			
			String add = "INSERT INTO COLLECTION (USERNAME, TIME_START, TIME_END, TYPE, QUERY) " +
					"VALUES (?,?,?,?,?);";
			
			PreparedStatement psmt = c.prepareStatement(add);
			
			// search.getType() 
			if(search instanceof HistoricSearch){
				type = "Historic";
			}
			else if(search instanceof LiveSearch) {
				type = "Live";
			}
			
			psmt.setString(1, user);
			psmt.setTimestamp(2,  start);
			psmt.setTimestamp(3, end);
			psmt.setString(4, type);
			psmt.setString(5, search.getKeyword());
			
			psmt.executeUpdate();
			psmt.close();
		
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	/**
	 * Add a tweet from a search in the database
	 */
	public static void addTweet(Status tweet, TwitterSearch search) { //REVIEW PARAMS, seguramente será simplemente "tweet" y "collection_id"
		
		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			
			String add = "INSERT INTO TWEET (TWEET_ID, COLLECTION_ID, AUTHOR, CREATED_AT, TEXT_PRINTABLE) " +
					"VALUES (?,?,?,?,?);"; // faltará añadir el raw tweet!!! // Pensar que hacer con city y country
			
			PreparedStatement psmt = c.prepareStatement(add);
			
			Integer collection_id = getCollection(search);
			
			java.util.Date utilStartDate = tweet.getCreatedAt();
			java.sql.Date sqlStartDate = new java.sql.Date(utilStartDate.getTime());
			
//			System.out.println("NEXT TWEET.\n");
			
			
			psmt.setLong(1, tweet.getId());		// id of the tweet in Twitter (better to be a String)
			psmt.setInt(2, collection_id);
//			psmt.setString(3, raw_tweet);		// JSON ?¿
			psmt.setString(3, tweet.getUser().getScreenName());
			psmt.setDate(4, sqlStartDate );		// time when tweet was created (tweet.getCreatedAt() is a String!! BE CAREFULL 
			psmt.setString(5, tweet.getText());	// text of the tweet (tweet.getText() ?¿)
/*			
			if(tweet.getPlace().getName()!=null) {
				psmt.setString(6,  tweet.getPlace().getName()); // a veces salen null
			}
			if(tweet.getPlace().getCountry()!=null) {
				psmt.setString(7,  tweet.getPlace().getCountry()); // a veces salen null
			}
*/
			int nrows = psmt.executeUpdate();
//			System.out.printf("Stmt '%s' affected '%d' rows, collection '%d'\n", psmt.toString(), nrows, collection_id);
			psmt.close();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	/**
	 * Get a specific search from the history list
	 */
	public static Integer getCollection(TwitterSearch search) {
		Statement stmt = null;

		try {
			if(c.isClosed()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
			}
			Class.forName(className);
			stmt = c.createStatement();

			String select = "SELECT collection_id FROM collection WHERE query=\""+search.getKeyword()+"\" "; /*&& Math.max(time_end)*/
			
			ResultSet rs = stmt.executeQuery(select);
			
			while (rs.next()) {
                return rs.getInt("collection_id");
            }
			
			return null;

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
	}
	
	/**
	 * Get a specific tweet from the search window (creo que esto no hace mucha falta, ya veré)
	 */
	public static void getTweet() {
		// NO AÑADIR stmt.close() !!!!!!!!!!!!!
	}
	
	
	/**
	 * Export data in a CSV
	 */
	public static void exportCSV(String query) {
		Statement stmt;
		String exp;
        String filename = "jdbc:sqlite:src/main/resources/20180405_"+query;
        try {
            stmt = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
             
            //For comma separated file
            exp = "SELECT author,text OUTFILE  '"+filename+
                    "' FIELDS TERMINATED BY ',' FROM tweet t";
            stmt.executeQuery(exp);
        } catch(Exception e) {
            e.printStackTrace();
            stmt = null;
        }
	}
}
