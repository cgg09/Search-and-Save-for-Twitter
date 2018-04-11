package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Vector;

import application.Main;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.QueryResult;
import twitter4j.Status;

public class DBCollection {
	
	private Connection c;
	
	private ObjectProperty<LocalDateTime> start_t;
	
	private ObjectProperty<LocalDateTime> end_t;

	private String type;
	
	private StringProperty query;
	
	private List<Status> tweets;
	
	int id = 0;
	
	public DBCollection(String type) {
		c = Main.getDatabaseDAO().getConnection();
		this.type = type;
		this.query = new SimpleStringProperty("");
		this.start_t = new SimpleObjectProperty<LocalDateTime>();
		this.end_t = new SimpleObjectProperty<LocalDateTime>();
		this.tweets = new Vector<Status>();
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
		
	public void addTweets(QueryResult tweetList) {
		this.tweets.addAll(tweetList.getTweets());
	}
	
	public void addData(Timestamp start, Timestamp end, String user) {
		try {
			addNewCollection(start, end, user);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
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
	 * @param search
	 * @param user
	 */
	public void addNewCollection(Timestamp start, Timestamp end, String user) {
		
		String type = "";
		
		try {
			
			String add = "INSERT INTO COLLECTION (USERNAME, TIME_START, TIME_END, TYPE, QUERY) " +
					"VALUES (?,?,?,?,?);";

			PreparedStatement psmt = c.prepareStatement(add);
			
			psmt.setString(1, user);
			psmt.setTimestamp(2,  start);
			psmt.setTimestamp(3, end);
			psmt.setString(4, type);
			psmt.setString(5, getQuery());
			
			psmt.executeUpdate();
			psmt.close();
			
			setStart(start.toLocalDateTime());
			setEnd(end.toLocalDateTime());
		
			//System.out.println("Start: "+start_t+". End: "+end_t+". Query: "+query.getValue()+".");
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a tweet from a search in the database
	 */
	public void addTweet(Status tweet) {
		
		try {
			
			id = this.getIdCollection();
			
			if(tweetExists(tweet)){
				//System.out.println("Tweet repeated :o");
				return;
			}
			
			//id =  //
			
			String add = "INSERT INTO TWEET (TWEET_ID, COLLECTION_ID, AUTHOR, CREATED_AT, TEXT_PRINTABLE) " +
					"VALUES (?,?,?,?,?);"; // faltará añadir el raw tweet!!! // quitar city y country
			
			PreparedStatement psmt_tweet = c.prepareStatement(add);
			
			
			
			java.util.Date utilStartDate = tweet.getCreatedAt();
			java.sql.Date sqlStartDate = new java.sql.Date(utilStartDate.getTime());
			
			psmt_tweet.setLong(1, tweet.getId());
			psmt_tweet.setInt(2, id);
//			psmt.setString(3, raw_tweet);		// JSON !!
			psmt_tweet.setString(3, tweet.getUser().getScreenName());
			psmt_tweet.setDate(4, sqlStartDate );		// transform it in a Date !!!
			psmt_tweet.setString(5, tweet.getText());	// PARSE TEXT !!

			psmt_tweet.executeUpdate();
			
		} catch ( Exception e ) {
			System.err.println( "Maybe is it here? "+e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Get a specific search from the history list
	 */
	public Integer getIdCollection() {

		try {

			//String select = "SELECT collection_id FROM COLLECTION WHERE time_start=\""+start_t+"\" AND time_end=\""+end_t+"\" AND query=\""+query+"\" ";
			// hacer que seleccione la collection con el tiempo mayor O arreglar timestamps java - sql
			
			String select = "SELECT collection_id FROM COLLECTION WHERE query=\""+query.getValue()+"\" ";
			//System.out.println(select);
			ResultSet rs = c.createStatement().executeQuery(select);
			//System.out.println(rs.getInt("collection_id"));
			while (rs.next()) {
                return rs.getInt("collection_id");
            }
			
			return null;

		} catch ( Exception e ) {
			System.err.println( "Where's Wally? "+e.getClass().getName() + ": " + e.getMessage() );
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
//			tweets.addAll(rst.getString("raw_tweet")); --> me falta añadir el JSON
			
		} catch(Exception e) {
			System.err.println(e.getClass().getName()+": "+e.getMessage());
		}
		
	}
	

}
