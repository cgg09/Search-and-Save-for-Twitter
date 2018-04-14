package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

import application.Main;
import application.utils.DisplayableTweet;
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
	private List<DisplayableTweet> currentTweets;

	private String addCollection = "INSERT INTO collection (USERNAME, TIME_START, TIME_END, TYPE, QUERY) "
			+ "VALUES (?,?,?,?,?);";
	private String addTweet = "INSERT INTO tweet (TWEET_ID, COLLECTION_ID, RAW_TWEET, AUTHOR, CREATED_AT, TEXT_PRINTABLE) "
			+ "VALUES (?,?,?,?,?,?);";

	// private String selectId = "SELECT collection_id FROM collection WHERE
	// query=\"";//+query.getValue()+"\" ";

	// private String selectCollection = "SELECT * FROM collection WHERE
	// collection_id=\"";//+id+"\" ";

	// private String updateTweets = "SELECT created_at, author, text_printable FROM
	// tweet WHERE collection_id=\"";//+id+"\" ";

	public DBCollection(String type) {
		c = Main.getDatabaseDAO().getConnection();
		this.type = type;
		this.query = new SimpleStringProperty("");
		this.start_t = new SimpleObjectProperty<LocalDateTime>();
		this.end_t = new SimpleObjectProperty<LocalDateTime>();
		this.tweets = new Vector<Status>();
		this.currentTweets = new Vector<DisplayableTweet>();
	}

	public int getId() {
		return id;
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

	public List<Status> getTweetList() {
		return tweets;
	}

	public void addTweets(QueryResult queryResult) {
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
	 * @param user
	 */
	public void addData(Timestamp start, Timestamp end, String user) {

		setStart(start.toLocalDateTime());
		setEnd(end.toLocalDateTime());

		// FIXME id should be captured here
		addNewCollection(user);

		id = this.getIdCollection();

		for (Status tweet : tweets) {
			addTweet(tweet);
		}
	}

	/**
	 * Add a new search to the database
	 * 
	 * @param user
	 */
	public void addNewCollection(String user) {

		PreparedStatement psmt;
		try {
			psmt = c.prepareStatement(addCollection);
			psmt.setString(1, user);
			psmt.setString(2, getStart().toString());
			psmt.setString(3, getEnd().toString());
			psmt.setString(4, type);
			psmt.setString(5, getQuery());

			psmt.executeUpdate();
			psmt.close();
		} catch (SQLException e) {
			// FIXME throw new DatabaseWriteException
			e.printStackTrace();
		}

	}

	/**
	 * Add a tweet from a search in the database
	 * 
	 * @param tweet
	 */
	public void addTweet(Status tweet) {

		JSONObject json = new JSONObject(tweet);

		LocalDateTime createdAt = tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		PreparedStatement psmt_tweet;

		try {
			psmt_tweet = c.prepareStatement(addTweet);
			psmt_tweet.setLong(1, tweet.getId());
			psmt_tweet.setInt(2, id);
			psmt_tweet.setString(3, json.toString());
			psmt_tweet.setString(4, tweet.getUser().getScreenName());
			psmt_tweet.setString(5, createdAt.toString());
			psmt_tweet.setString(6, tweet.getText()); // PARSE TEXT !!

			psmt_tweet.executeUpdate();
		} catch (SQLException e) {
			// FIXME throw new DatabaseWriteException
			e.printStackTrace();
		}

		DisplayableTweet t = new DisplayableTweet(createdAt, tweet.getUser().getScreenName(), tweet.getText());
		currentTweets.add(t);
	}

	/**
	 * Get the id of a specific collection
	 */
	public Integer getIdCollection() { // seleccionar la collection con el tiempo mayor O arreglar timestamps java -
										// sql
		String selectId = "SELECT collection_id FROM collection WHERE query=\"" + query.getValue() + "\" ";

		ResultSet rs;
		try {
			rs = c.createStatement().executeQuery(selectId);
			while (rs.next()) {
				return rs.getInt("collection_id");
			}
		} catch (SQLException e) {
			// FIXME throw new DatabaseReadException
			e.printStackTrace();
		}
		return null; // FIXME throw new DatabaseReadException
	}

	public boolean tweetExists(Status tweet) {

		String select = "SELECT * FROM tweet WHERE collection_id=\"" + id + "\" AND tweet_id=\"" + tweet.getId()
				+ "\" ";
		ResultSet rs;
		try {
			rs = c.createStatement().executeQuery(select);
			if (rs != null) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			// FIXME throw new DatabaseReadException
			e.printStackTrace();
		}
		return false; // FIXME throw new DatabaseReadException
	}

	public void updateCollection() {

		id = getIdCollection();

		System.out.println("Update: " + id);

		String selectCollection = "SELECT * FROM collection WHERE collection_id=\"" + id + "\" ";
		ResultSet rsc;
		try {
			rsc = c.createStatement().executeQuery(selectCollection);
			setStart(LocalDateTime.parse(rsc.getString("time_start")));
			setEnd(LocalDateTime.parse(rsc.getString("time_end")));
			type = rsc.getString("type");
			setQuery(rsc.getString("query"));
		} catch (SQLException e) {
			// FIXME throw new DatabaseReadException
			e.printStackTrace();
		}

		updateTweets();
	}

	public void updateTweets() {

		currentTweets.clear();

		String updateTweets = "SELECT created_at, author, text_printable FROM tweet WHERE collection_id=\"" + id
				+ "\" ";

		ResultSet rst;
		try {
			rst = c.createStatement().executeQuery(updateTweets);
			while (rst.next()) {
				DisplayableTweet t = new DisplayableTweet(LocalDateTime.parse(rst.getString("created_at")), rst.getString("author"),
						rst.getString("text_printable"));
				currentTweets.add(t);
			}
		} catch (SQLException e) {
			// FIXME throw new DatabaseReadException
			e.printStackTrace();
		}

	}

}
