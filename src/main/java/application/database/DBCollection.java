package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

//import com.twitter.twittertext.TwitterTextConfiguration;
//import com.twitter.twittertext.TwitterTextParser;

import application.Main;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.utils.DisplayableTweet;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.JSONObject;
import twitter4j.QueryResult;
import twitter4j.Status;

public class DBCollection {

	int T = 0;
	int F = 0;

	private int id = 0;

	private Connection c;
	private StringProperty start_t;
	private StringProperty end_t;
	private String type;
	private StringProperty query;
	private List<Status> tweets;
	private List<DisplayableTweet> currentTweets;

	// Queries
	private String addCollection = "INSERT INTO collection (USERNAME, TIME_START, TIME_END, TYPE, QUERY) "
			+ "VALUES (?,?,?,?,?);";
	private String addTweet = "INSERT INTO tweet (TWEET_ID, COLLECTION_ID, RAW_TWEET, AUTHOR, CREATED_AT, TEXT_PRINTABLE, RETWEET) "
			+ "VALUES (?,?,?,?,?,?,?);";

	private String queryExists = "SELECT collection_id FROM collection WHERE query= ?";// AND time_start= ?";

	private String checkTweet = "SELECT * FROM tweet WHERE collection_id= ? AND tweet_id= ?";

	private String updateCollection = "SELECT * FROM collection WHERE collection_id= ?";

	private String updateTweets = "SELECT tweet_id, created_at, author, text_printable, retweet FROM tweet WHERE collection_id= ?";

	private String delTweets = "DELETE FROM tweet WHERE collection_id = ?";

	private String delCol = "DELETE FROM collection where collection_id = ?";

	private String exportCol = "SELECT * FROM tweet WHERE collection_id= ?";

	public DBCollection(String type) {
		c = Main.getDatabaseDAO().getConnection();
		this.type = type;
		this.query = new SimpleStringProperty("");
		this.start_t = new SimpleStringProperty("");
		this.end_t = new SimpleStringProperty("");
		this.tweets = new Vector<Status>();
		this.currentTweets = new Vector<DisplayableTweet>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getStart() {
		return start_t.get();
	}

	public void setStart(String start) {
		this.start_t.set(start);
	}

	public StringProperty startProperty() {
		return start_t;
	}

	public String getEnd() {
		return end_t.get();
	}

	public void setEnd(String end) {
		this.end_t.set(end);
	}

	public StringProperty endProperty() {
		return end_t;
	}

	public List<Status> getTweetStatus() {
		return tweets;
	}

	public void saveTweetStatus(QueryResult queryResult) {
		this.tweets.addAll(queryResult.getTweets());
	}

	public List<DisplayableTweet> getCurrentTweets() {
		return currentTweets;
	}

	/**
	 * Add info about the search in the Database
	 * 
	 * @param start
	 * @param end
	 * @param dbUserDAO
	 * @throws ParseException
	 */
	public void addData(Timestamp start, Timestamp end, DBUserDAO dbUserDAO) {

		// Converting start_time
		LocalDateTime start_time = LocalDateTime.parse(start.toLocalDateTime().toString());
		setStart(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(start_time));

		// Converting end_time
		LocalDateTime end_time = LocalDateTime.parse(end.toLocalDateTime().toString());
		setEnd(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(end_time));

		try {
			id = addNewCollection(dbUserDAO);
		} catch (DatabaseWriteException e) {
			e.printStackTrace();
		}

		for (Status tweet : tweets) {
			try {
				addTweet(tweet);
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a new search to the database
	 * 
	 * @param user
	 * @throws DatabaseWriteException
	 */
	public Integer addNewCollection(DBUserDAO user) throws DatabaseWriteException {

		PreparedStatement psmt = null;
		ResultSet rsk = null;
		try {
			psmt = c.prepareStatement(addCollection);
			psmt.setString(1, user.getUser());
			psmt.setString(2, getStart());
			psmt.setString(3, getEnd());
			psmt.setString(4, type);
			psmt.setString(5, getQuery());

			psmt.executeUpdate();

			rsk = psmt.getGeneratedKeys();
			if (rsk.next()) {
				setId(rsk.getInt(1));
			}

			psmt.close();
			return rsk.getInt(1);

		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error saving the collection info.", e);
		}
	}

	/**
	 * Add a tweet from a search in the database
	 * 
	 * @param tweet
	 * @throws DatabaseWriteException
	 * @throws DatabaseReadException
	 */
	public void addTweet(Status tweet) throws DatabaseWriteException {

		JSONObject json = new JSONObject(tweet);
		int retweet = 0;
		boolean RT = false;

		LocalDateTime createdAt = tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		String created_at = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(createdAt);

		if (tweet.getRetweetedStatus() != null) {
			retweet = 1;
			RT = true;
		}

		// TwitterTextParser tweetParser = new TwitterTextParser();
		// TwitterTextConfiguration tweetParser = new TwitterTextConfiguration();
		// tweetParser.getClass()
		// String text = tweet.getText().replace("\n", "").replace("\r", "");

		/*
		 * if(text.contains("\n")) {
		 * System.out.println("Newline in tweet: "+tweet.getText()); }
		 */

		PreparedStatement psmt_tweet = null;

		try {
			psmt_tweet = c.prepareStatement(addTweet);
			psmt_tweet.setLong(1, tweet.getId());
			psmt_tweet.setInt(2, id);
			psmt_tweet.setString(3, json.toString());
			psmt_tweet.setString(4, tweet.getUser().getScreenName());
			psmt_tweet.setString(5, created_at);
			psmt_tweet.setString(6, tweet.getText()); // FIXME PARSE TEXT !!
			psmt_tweet.setInt(7, retweet);

			psmt_tweet.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error saving the tweet info.", e);
		}

		DisplayableTweet t = new DisplayableTweet(tweet.getId(), created_at, tweet.getUser().getScreenName(), tweet.getText(), RT);
		currentTweets.add(t);
	}

	/**
	 * Get the id of a specific collection to check if it exists
	 * 
	 * @throws DatabaseReadException
	 * @throws DataNotFoundException
	 */
/*	public Integer collectionExists() throws DatabaseReadException, DataNotFoundException {

		PreparedStatement psid = null;
		ResultSet rs = null;
		try {
			psid = c.prepareStatement(queryExists);
			psid.setString(1, query.getValue());
			psid.setString(2, getStart());
			rs = psid.executeQuery();
			while (rs.next()) {
				return rs.getInt("collection_id");
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error searching the collection id.", e);
		}
		//return null;
		throw new DataNotFoundException("This collection does not exist"); // FIXME alert, not an exception
	}
*/
	public boolean collectionExists() throws DatabaseReadException {
		PreparedStatement psid = null;
		ResultSet rs = null;
		try {
			psid = c.prepareStatement(queryExists);
			psid.setString(1, query.getValue());
			psid.setString(2, getStart());
			rs = psid.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error searching the collection id.", e);
		}
		return false;
	}
	
	public boolean tweetExists(Status tweet) throws DatabaseReadException, DataNotFoundException {

		PreparedStatement psct = null;
		ResultSet rsct = null;
		try {
			psct = c.prepareStatement(checkTweet);
			psct.setInt(1, id);
			psct.setLong(2, tweet.getId());
			rsct = psct.executeQuery();
			if (rsct != null) {
				return true;
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error searching the tweet.", e);
		}
		throw new DataNotFoundException("This tweet does not exist");
	}

	public void updateCollection() throws DatabaseReadException {

		PreparedStatement psuc = null;
		ResultSet rsc = null;
		try {
			psuc = c.prepareStatement(updateCollection);
			psuc.setInt(1, id);
			rsc = psuc.executeQuery();
			setStart(rsc.getString("time_start"));
			setEnd(rsc.getString("time_end"));
			type = rsc.getString("type");
			setQuery(rsc.getString("query"));
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error reading the collection info.", e);
		}

		updateTweets();
	}

	public void updateTweets() throws DatabaseReadException {

		if (!currentTweets.isEmpty()) {
			return;
		}

		boolean RT = false;

		PreparedStatement psut = null;
		ResultSet rst = null;
		try {
			psut = c.prepareStatement(updateTweets);
			psut.setInt(1, id);
			rst = psut.executeQuery();
			while (rst.next()) {
				if (rst.getInt("retweet") == 1) {
					RT = true;
				}

				DisplayableTweet t = new DisplayableTweet(rst.getLong("tweet_id"), rst.getString("created_at"), rst.getString("author"),
						rst.getString("text_printable"), RT);
				currentTweets.add(t);
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error reading the tweets info.", e);

		}

	}

	public void sortTweets() { // TODO sort tweet list descending by date
		// currentTweets ...
	}

	public void deleteCollection() throws DatabaseWriteException {

		PreparedStatement psdt = null;
		try {
			psdt = c.prepareStatement(delTweets);
			psdt.setInt(1, id);
			psdt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error deleting the tweets of the collection.", e);
		}
		PreparedStatement psdc = null;
		try {
			psdc = c.prepareStatement(delCol);
			psdc.setInt(1, id);
			psdc.executeUpdate();

		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error deleting the collection info.", e);
		}

	}

	public String exportTweets() throws DatabaseReadException {
		PreparedStatement pstmt = null;
		ResultSet rsExp = null;
		String tweetsExported = "tweet_id,collection_id,raw_tweet,author,created_at,text_printable,retweet\n";
		try {
			pstmt = c.prepareStatement(exportCol);

			pstmt.setInt(1, id);

			rsExp = pstmt.executeQuery();

			while (rsExp.next()) {
				tweetsExported += rsExp.getInt("tweet_id") + "," + rsExp.getInt("collection_id") + ","
						+ rsExp.getString("raw_tweet") + "," + rsExp.getString("author") + ","
						+ rsExp.getString("created_at") + "," + rsExp.getString("text_printable") + ","
						+ rsExp.getInt("retweet") + "\n";
			}

		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error while exporting the tweets from the database.", e);
		}
		return tweetsExported;
	}

}
