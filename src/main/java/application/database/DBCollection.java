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
import twitter4j.Status;

public class DBCollection {
	
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
			
			System.out.println("Type of search: "+search.getClass().getName()); // a ver si así coge bien el tipo
			
			// search.getType() 
			if(search instanceof HistoricSearch){
				type = "Historic";
			}
			else if(search instanceof LiveSearch) {
				type = "Live";
			}
			
			System.out.println("Username="+user);
			
			psmt.setString(1, user);
			psmt.setTimestamp(2,  start);
			psmt.setTimestamp(3, end);
			psmt.setString(4, type);
			psmt.setString(5, search.getQuery());
			
			psmt.executeUpdate();
			psmt.close();
		
		} catch ( Exception e ) {
			System.err.println( "Hi, is it here? "+e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	/**
	 * Add a tweet from a search in the database
	 */
	public void addTweet(Status tweet, TwitterSearch search) { //REVIEW PARAMS, seguramente será simplemente "tweet" y "collection_id"
		
		try {
			
			String add = "INSERT INTO TWEET (TWEET_ID, COLLECTION_ID, AUTHOR, CREATED_AT, TEXT_PRINTABLE) " +
					"VALUES (?,?,?,?,?);"; // faltará añadir el raw tweet!!! // Pensar que hacer con city y country
			
			PreparedStatement psmt = c.prepareStatement(add);
			
			Integer collection_id = getCollection(search);
			
//			System.out.println(add);
			
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
			System.err.println( "Maybe is it here? "+e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	/**
	 * Get a specific search from the history list
	 */
	public Integer getCollection(TwitterSearch search) {

		Statement stmt = null;

		try {

			stmt = c.createStatement();

			String select = "SELECT collection_id FROM collection WHERE query=\""+search.getQuery()+"\" "; // cambiar método: buscar por query y por fecha/hora de búsqueda
			
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
	
	
	
	

}
