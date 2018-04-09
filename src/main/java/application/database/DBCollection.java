package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import application.Main;
import application.model.HistoricSearch;
import application.model.LiveSearch;
import application.model.TwitterSearch;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.Status;

public class DBCollection {
	
	private int id;
	
	private StringProperty query = new SimpleStringProperty("");;
	
	private Timestamp start_t;
	
	private Timestamp end_t;
	
	private String user;
	
	private Connection c;
	
	public DBCollection() {
		c = Main.getDatabaseDAO().getConnection();
	}
	
	/**
	 * Add a new search to the database
	 * @param search
	 * @param user
	 */
	public void addNewCollection(TwitterSearch search, Timestamp start, Timestamp end, String user) {
		
		String type = "";
		
		try {
			
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
			psmt.setString(5, search.getQuery());
			
			psmt.executeUpdate();
			psmt.close();
			
			start_t = start;
			end_t = end;
			
			query = search.queryProperty();
		
			System.out.println("Start: "+start_t+". End: "+end_t+". Query: "+query+".");
			
		} catch ( Exception e ) {
			System.err.println( "Hi, is it here? "+e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a tweet from a search in the database
	 */
	public void addTweet(Status tweet, TwitterSearch search) {
		
		try {
			
			if(tweetExists(tweet)){
				//System.out.println("Tweet repeated :o");
				return;
			}
			
			id = this.getCollection();
			
			String add = "INSERT INTO TWEET (TWEET_ID, COLLECTION_ID, AUTHOR, CREATED_AT, TEXT_PRINTABLE) " +
					"VALUES (?,?,?,?,?);"; // faltará añadir el raw tweet!!! // quitar city y country
			
			PreparedStatement psmt = c.prepareStatement(add);
			
			int collection_id = id;
			
			java.util.Date utilStartDate = tweet.getCreatedAt();
			java.sql.Date sqlStartDate = new java.sql.Date(utilStartDate.getTime());
			
			psmt.setLong(1, tweet.getId());
			psmt.setInt(2, collection_id);
//			psmt.setString(3, raw_tweet);		// JSON !!
			psmt.setString(3, tweet.getUser().getScreenName());
			psmt.setDate(4, sqlStartDate );		// transform it in a Date !!!
			psmt.setString(5, tweet.getText());	// PARSE TEXT !!

			psmt.executeUpdate();

			//psmt.close();
			
		} catch ( Exception e ) {
			System.err.println( "Maybe is it here? "+e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	/**
	 * Get a specific search from the history list
	 */
	public Integer getCollection() {

		Statement stmt = null;

		try {

			stmt = c.createStatement();
			String select = "SELECT collection_id FROM COLLECTION WHERE time_start=\""+start_t+"\" AND time_end=\""+end_t+"\" AND query=\""+query+"\" ";
			ResultSet rs = stmt.executeQuery(select);
			System.out.println(rs.getInt("collection_id"));
			while (rs.next()) {
                return rs.getInt("collection_id");
            }
			
			return null;

		} catch ( Exception e ) {
			System.err.println( "Where's Wally? "+e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
	}
	
	public Timestamp getStart() {
		return start_t;
	}
	
	public Timestamp getEnd() {
		return end_t;
	}
	
	public String getQuery() {
		return query.get();
	}
	
	public StringProperty queryProperty() {
		return query;
	}
	
	public boolean tweetExists(Status tweet) {
		
		try {
			String select = "SELECT * FROM tweet WHERE tweet_id=\""+tweet.getId()+"\" ";
			ResultSet rs = c.createStatement().executeQuery(select);
			if(rs!=null) {
				return true;
			} else {
				return false;
			}
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
	}
	
	public void updateCollection() {
		
	}
	
	
	
	

}
