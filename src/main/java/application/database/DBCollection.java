package application.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Vector;

import application.Main;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
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

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	// Queries
	private String addCollection = "INSERT INTO collection (USERNAME, TIME_START, TIME_END, TYPE, QUERY) "
			+ "VALUES (?,?,?,?,?);";
	private String addTweet = "INSERT INTO tweet (TWEET_ID, COLLECTION_ID, RAW_TWEET, AUTHOR, CREATED_AT, TEXT_PRINTABLE, RETWEET) "
			+ "VALUES (?,?,?,?,?,?,?);";

	// private String selectId = "SELECT collection_id FROM collection WHERE
	// query=\"";//+query.getValue()+"\" ";

	// private String selectCollection = "SELECT * FROM collection WHERE
	// collection_id=\"";//+id+"\" ";

	// private String updateTweets = "SELECT created_at, author, text_printable FROM
	// tweet WHERE collection_id=\"";//+id+"\" ";
	private String delTweets = "DELETE FROM tweet WHERE collection_id = ?";
	private String delCol = "DELETE FROM collection where collection_id = ?";

	private String exportCol = "SELECT * FROM tweet WHERE collection_id= ?";

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
	 * @param user
	 * @throws ParseException
	 */
	public void addData(Timestamp start, Timestamp end, String user) {

		setStart(start.toLocalDateTime());
		setEnd(end.toLocalDateTime());

		try {
			id = addNewCollection(user);
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
	public Integer addNewCollection(String user) throws DatabaseWriteException {

		PreparedStatement psmt;
		ResultSet rsk = null;
		try {
			psmt = c.prepareStatement(addCollection);
			psmt.setString(1, user);
			psmt.setString(2, getStart().toString());
			psmt.setString(3, getEnd().toString());
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

		PreparedStatement psmt_tweet;

		if (tweet.getRetweetedStatus() != null) {
			retweet = 1;
			RT = true;
		}

		try {
			psmt_tweet = c.prepareStatement(addTweet);
			psmt_tweet.setLong(1, tweet.getId());
			psmt_tweet.setInt(2, id);
			psmt_tweet.setString(3, json.toString());
			psmt_tweet.setString(4, tweet.getUser().getScreenName());
			psmt_tweet.setString(5, createdAt.toString());
			psmt_tweet.setString(6, tweet.getText()); // FIXME PARSE TEXT !!
			psmt_tweet.setInt(7, retweet);

			psmt_tweet.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error saving the tweet info.", e);
		}

		// FIXME parse date in format yyyy-MM-dd HH:mm
		DisplayableTweet t = new DisplayableTweet(createdAt, tweet.getUser().getScreenName(), tweet.getText(), RT);
		currentTweets.add(t);
	}

	/**
	 * Get the id of a specific collection
	 * 
	 * @throws DatabaseReadException
	 * @throws DataNotFoundException
	 */
	public Integer getIdCollection() throws DatabaseReadException, DataNotFoundException { // TODO collection_id + start

		String selectId = "SELECT collection_id FROM collection WHERE query=\"" + query.getValue() + "\" ";

		ResultSet rs;
		try {
			rs = c.createStatement().executeQuery(selectId);
			while (rs.next()) {
				return rs.getInt("collection_id");
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error searching the collection id.", e);
		}
		throw new DataNotFoundException("This collection does not exist");
	}

	public boolean tweetExists(Status tweet) throws DatabaseReadException, DataNotFoundException {

		String select = "SELECT * FROM tweet WHERE collection_id=\"" + id + "\" AND tweet_id=\"" + tweet.getId()
				+ "\" ";
		ResultSet rs;
		try {
			rs = c.createStatement().executeQuery(select);
			if (rs != null) {
				return true;
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error searching the tweet.", e);
		}
		throw new DataNotFoundException("This tweet does not exist");
	}

	public void updateCollection() throws DatabaseReadException {

		String selectCollection = "SELECT * FROM collection WHERE collection_id=\"" + id + "\" ";
		ResultSet rsc;
		try {
			rsc = c.createStatement().executeQuery(selectCollection);
			setStart(LocalDateTime.parse(rsc.getString("time_start")));
			setEnd(LocalDateTime.parse(rsc.getString("time_end")));
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

		String updateTweets = "SELECT created_at, author, text_printable, retweet FROM tweet WHERE collection_id=\""
				+ id + "\" ";

		ResultSet rst;
		try {
			rst = c.createStatement().executeQuery(updateTweets);
			while (rst.next()) {
				if (rst.getInt("retweet") == 1) {
					RT = true;
				}

				// FIXME parse date in format yyyy-MM-dd HH:mm
				DisplayableTweet t = new DisplayableTweet(LocalDateTime.parse(rst.getString("created_at")),
						rst.getString("author"), rst.getString("text_printable"), RT);
				currentTweets.add(t);
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error reading the tweets info.", e);

		}

	}

	public void sortTweets() { // TODO sort tweet list descending by date
		// currentTweets ...
	}

	public String parseTime(LocalDateTime date) {

		String d = null;

		try {
			d = formatter.format(formatter.parse(date.toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public void deleteCollection() throws DatabaseWriteException {

		try {
			PreparedStatement psdt = c.prepareStatement(delTweets);// .executeQuery(delTweets);
			psdt.setInt(1, id);
			psdt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error deleting the tweets of the collection.", e);
		}
		try {
			PreparedStatement psdc = c.prepareStatement(delCol);
			psdc.setInt(1, id);
			psdc.executeUpdate();

		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error deleting the collection info.", e);
		}

	}

	public String exportTweets() throws DatabaseReadException {
		PreparedStatement pstmt;
		String tweetsExported = "tweet_id,collection_id,raw_tweet,author,created_at,text_printable,retweet\n";
		try {
			pstmt = c.prepareStatement(exportCol);

			pstmt.setInt(1, id);

			ResultSet rsExp = pstmt.executeQuery();

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
