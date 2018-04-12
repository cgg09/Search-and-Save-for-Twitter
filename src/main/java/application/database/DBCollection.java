package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Vector;

import com.twitter.twittertext.TwitterTextParser;

import application.Main;
import application.utils.Tweet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.JSONObject;
import twitter4j.QueryResult;
import twitter4j.Status;

public class DBCollection {
	
	private int id = 0;
	
	private Connection c;
	private ObjectProperty<LocalDateTime> start_t;
	private ObjectProperty<LocalDateTime> end_t;
	private String type;
	private StringProperty query;
	private List<Status> tweets;
	private List<Tweet> currentTweets;
	
		
	public DBCollection(String type) {
		c = Main.getDatabaseDAO().getConnection();
		this.type = type;
		this.query = new SimpleStringProperty("");
		this.start_t = new SimpleObjectProperty<LocalDateTime>();
		this.end_t = new SimpleObjectProperty<LocalDateTime>();
		this.tweets = new Vector<Status>();
		this.currentTweets = new Vector<Tweet>();
	}
	
	public String getType() {
		return type;
	}
	
	public String getQuery() {
		return query.get();
	}
	
	public void setQuery(String keyword) {
		this.query.set(keyword);
	}
	
	public StringProperty queryProperty() {
		return query;
	}
	
	public LocalDateTime getStart() {
        return start_t.get();
    }

    public void setStart(LocalDateTime start) {
        this.start_t.set(start);
    }

    public ObjectProperty<LocalDateTime> startProperty() {
        return start_t;
    }
    
    public LocalDateTime getEnd() {
        return end_t.get();
    }

    public void setEnd(LocalDateTime end) {
        this.end_t.set(end);
    }

    public ObjectProperty<LocalDateTime> endProperty() {
        return end_t;
    }	
	
	public List<Status> getTweetList(){
		return tweets;
	}
	
	public void addTweets(QueryResult queryResult) {
		this.tweets.addAll(queryResult.getTweets());
	}
	
	public List<Tweet> getCurrentTweets(){
		return currentTweets;
	}

	/**
	 * Add info about the search in the Database
	 * @param start
	 * @param end
	 * @param user
	 */
	public void addData(Timestamp start, Timestamp end, String user) {
		
		setStart(start.toLocalDateTime());
		setEnd(end.toLocalDateTime());
		
		try {
			addNewCollection(user);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		id = this.getIdCollection();
		
		try {
			for(Status tweet : tweets) {
				addTweet(tweet);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a new search to the database
	 * @param user
	 */
	public void addNewCollection(String user) {
		
		try {
			
			String add = "INSERT INTO collection (USERNAME, TIME_START, TIME_END, TYPE, QUERY) " +
					"VALUES (?,?,?,?,?);";

			PreparedStatement psmt = c.prepareStatement(add);
			
			psmt.setString(1, user);
			psmt.setString(2,  getStart().toString());
			psmt.setString(3, getEnd().toString());
			psmt.setString(4, type);
			psmt.setString(5, getQuery());
			
			psmt.executeUpdate();
			psmt.close();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a tweet from a search in the database
	 * @param tweet
	 */
	public void addTweet(Status tweet) {
		
		try {
			
			/*
			if(tweetExists(tweet)){
				return;
			}*/ // no sé si funciona, seguir probando
			
			JSONObject json = new JSONObject(tweet);
			
			LocalDateTime createdAt = tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			
			
			//java.sql.Date sqlStartDate = tweet.getCreatedAt();

			String add = "INSERT INTO tweet (TWEET_ID, COLLECTION_ID, RAW_TWEET, AUTHOR, CREATED_AT, TEXT_PRINTABLE) " +
					"VALUES (?,?,?,?,?,?);";

			PreparedStatement psmt_tweet = c.prepareStatement(add);
			psmt_tweet.setLong(1, tweet.getId());
			psmt_tweet.setInt(2, id);
			psmt_tweet.setString(3, json.toString());
			psmt_tweet.setString(4, tweet.getUser().getScreenName());
			psmt_tweet.setString(5, createdAt.toString());
			psmt_tweet.setString(6, tweet.getText());	// PARSE TEXT !! -> esto será lo que vaya a la currentView !!

			psmt_tweet.executeUpdate();
			
			Tweet t = new Tweet(createdAt, tweet.getUser().getScreenName(), tweet.getText());
			currentTweets.add(t);
			
			
		} catch ( Exception e ) {
			System.err.println( "Maybe is it here? "+e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Get a specific search from the history list
	 */
	public Integer getIdCollection() {

		try {			
			// hacer que seleccione la collection con el tiempo mayor O arreglar timestamps java - sql
			String select = "SELECT collection_id FROM collection WHERE query=\""+query.getValue()+"\" ";
			ResultSet rs = c.createStatement().executeQuery(select);
			while (rs.next()) {
                return rs.getInt("collection_id");
            }
			
			return null;

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
	}
	
		
	public boolean tweetExists(Status tweet) {
		
		try {
			String select = "SELECT * FROM tweet WHERE collection_id=\""+id+"\" AND tweet_id=\""+tweet.getId()+"\" ";
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
		
		
		try {
			
			id = getIdCollection();
			
			String col = "SELECT * FROM collection WHERE collection_id=\""+id+"\" ";
			ResultSet rsc = c.createStatement().executeQuery(col);
			
			System.out.println(col);
			
			setStart(rsc.getTimestamp("time_start").toLocalDateTime());
			setEnd(rsc.getTimestamp("time_end").toLocalDateTime());
			
			type = rsc.getString("type");
			
			setQuery(rsc.getString("query"));
			
			//tweets.clear();
			System.out.println("Tweet list empty? "+tweets.isEmpty());
			getTweets();
			
					
		} catch(Exception e) {
			System.err.println(e.getClass().getName()+": "+e.getMessage());
		}
		
	}
	
	public void getTweets() {
		
		try {
			
			String select = "SELECT * FROM tweet WHERE collection_id=\""+id+"\" ";
			ResultSet rst = c.createStatement().executeQuery(select);
			//tweets.addAll(rst); // me falta añadir el JSON ?
			
		} catch(Exception e) {
			System.err.println(e.getClass().getName()+": "+e.getMessage());
		}
		
	}
	

}
