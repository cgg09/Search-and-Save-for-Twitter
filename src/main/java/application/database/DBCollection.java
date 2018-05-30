package application.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import application.Main;
import application.exceptions.AccessException;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.exceptions.NetworkException;
import application.exceptions.RateLimitException;
import application.utils.DisplayableTweet;
import application.view.HistoricViewController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import twitter4j.JSONObject;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

public class DBCollection {

	private int id = 0;

	private Connection c;
	private StringProperty start_t;
	private StringProperty end_t;
	private String type;
	private StringProperty query;
	private List<DisplayableTweet> currentTweets;
	private int download = 0;
	private boolean done = false;

	private boolean repeated = false;
	private int UNAUTHORIZED = 401;

	// Queries
	private String addCollection = "INSERT INTO collection (username, time_start, type, query) VALUES (?,?,?,?);";
	private String addTweet = "INSERT INTO tweet (tweet_id, collection_id, author, created_at, text_printable, retweet, raw_tweet) "
			+ "VALUES (?,?,?,?,?,?,?);";

	private String addTimeData = "UPDATE collection SET time_end = ? WHERE collection_id IN (?);";

	private String updateStartTime = "UPDATE collection SET time_start = ? WHERE collection_id IN (?);";
	
	private String queryExists = "SELECT collection_id FROM collection WHERE collection_id = ? AND query = ? AND username = ?";

	private String checkTweet = "SELECT * FROM tweet WHERE tweet_id = ?";

	private String updateCollection = "SELECT * FROM collection WHERE collection_id = ?";

	private String retrieveTweets = "SELECT tweet_id, created_at, author, text_printable, retweet FROM tweet "
			+ "WHERE collection_id = ? ORDER BY created_at DESC";

	private String getNewestTweet = "SELECT MAX(tweet_id) AS max_tweet_id FROM tweet WHERE collection_id = ?";

	private String delTweets = "DELETE FROM tweet WHERE collection_id = ?";

	private String delCol = "DELETE FROM collection where collection_id = ?";

	private String exportCol = "SELECT tweet_id, created_at, author, text_printable, raw_tweet FROM tweet WHERE collection_id= ?";

	public DBCollection(String type) {
		c = Main.getDatabaseDAO().getConnection();
		this.type = type;
		this.query = new SimpleStringProperty("");
		this.start_t = new SimpleStringProperty("");
		this.end_t = new SimpleStringProperty("");
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

	public List<DisplayableTweet> getCurrentTweets() {
		return currentTweets;
	}

	public boolean getRepeated() {
		return repeated;
	}

	public void setRepeated(boolean r) {
		this.repeated = r;
	}
	
	public int getDownloaded() {
		return download;
	}
	
	public void incrementDownloaded(int download) {
		this.download += download;
	}
	
	public boolean getDone() {
		return done;
	}

	public Boolean manageSearch(String userQuery) throws AccessException, RateLimitException, NetworkException {

		done = false;
		// Preparing the query for the twitter searcher
		setQuery(userQuery);
		//int list = getCurrentTweets().size();
		Query query = new Query();
		//String q = '"'+getQuery()+'"';
		//query.setQuery(q);
		query.setQuery(getQuery());
		
		
		long tid = 0;
		try {
			tid = getNewestTweet();
			System.out.println("Newest tweet-id:" + tid);
		} catch (DatabaseReadException e2) {
			e2.printStackTrace();
		}

		query.sinceId(tid);

		System.out.println("Searching...");
		

		QueryResult queryResult = null;

		Timestamp ts_start = new Timestamp(System.currentTimeMillis());
		
		if (!getRepeated()) {
			try {
				id = addNewCollection(Main.getDBUserDAO().getUser(), ts_start);
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				updateStartTime(ts_start);
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		}

		do {

			System.out.println("Issuing Twitter search");
			try {
				queryResult = Main.getTwitterSessionDAO().getTwitter().search(query);
			} catch (TwitterException e) {
				if (e.getStatusCode() == UNAUTHORIZED) {
					throw new AccessException(e.getErrorMessage(), e);
				} else if (e.getRateLimitStatus().getRemaining() == 0) {
					Integer time = e.getRateLimitStatus().getSecondsUntilReset();
					time = time / 60;

					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("RATE LIMIT FAILURE");
					alert.setHeaderText("Rate Limit searching tweets");
					alert.setContentText("You have exceeded rate limit. You have to wait " + time.toString()
							+ " seconds before continue searching.\n Please note that your current search will not be saved on the system.");
					alert.showAndWait();

					// throw new RateLimitException("You have exceeded rate limit",e);
				} else {
					throw new NetworkException(
							"You do not have internet connection. Please check it out before continue", e);
				}
			}
			int down = 0;
			boolean ds = false;
			System.out.println("Iterating through resulting tweets");
			for (Status tweet : queryResult.getTweets()) {
				try {
					ds = addTweet(tweet);
					
				} catch (DatabaseWriteException e) {
					e.printStackTrace();
				}
				if(ds) {
					down++;
				}
			}

			incrementDownloaded(down);
			System.out.println("Downloaded so far: " + getDownloaded());
		} while ((query = queryResult.nextQuery()) != null);

		Timestamp ts_end = new Timestamp(System.currentTimeMillis());

		try {
			addEndTime(ts_end);
		} catch (DatabaseWriteException e) {
			e.printStackTrace();
		}

		//int current = getCurrentTweets().size();
		//int downloaded = Math.abs(current - list);
		//setDownloaded(downloaded);
		
		System.out.println("Exiting manageSearch");
		//done = true; // FIXME --> quizás no importe si devolver true o false en el call()...
		return done;

	}
	
	
	public void updateStartTime(Timestamp start) throws DatabaseWriteException {

		// Converting start_time
		LocalDateTime start_time = LocalDateTime.parse(start.toLocalDateTime().toString());
		setStart(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(start_time));

		PreparedStatement psmt = null;
		try {
			psmt = c.prepareStatement(updateStartTime);
			psmt.setString(1, getStart());
			psmt.setInt(2, id);

			psmt.executeUpdate();

			psmt.close();

		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error saving the collection info.", e);
		}

	}


	/**
	 * Add info about the search in the Database
	 * 
	 * @param start
	 * @param end
	 * @param dbUserDAO
	 * @throws DatabaseWriteException
	 * @throws ParseException
	 */
	public void addEndTime(Timestamp end) throws DatabaseWriteException {

		// Converting end_time
		LocalDateTime end_time = LocalDateTime.parse(end.toLocalDateTime().toString());
		setEnd(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(end_time));

		PreparedStatement psmt = null;
		try {
			psmt = c.prepareStatement(addTimeData);
			psmt.setString(1, getEnd());
			psmt.setInt(2, id);

			psmt.executeUpdate();

			psmt.close();

		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error saving the collection info.", e);
		}

	}

	/**
	 * Add a new search to the database
	 * 
	 * @param user
	 * @throws DatabaseWriteException
	 * @throws ParseException
	 */
	public Integer addNewCollection(String user, Timestamp start) throws DatabaseWriteException {

		// Converting start_time
		LocalDateTime start_time = LocalDateTime.parse(start.toLocalDateTime().toString());
		setStart(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(start_time));		
		
		PreparedStatement psmt = null;
		ResultSet rsk = null;
		try {
			psmt = c.prepareStatement(addCollection);
			psmt.setString(1, user);
			psmt.setString(2, getStart());
			psmt.setString(3, type);
			psmt.setString(4, getQuery());

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
	 * @return 
	 * @throws DatabaseWriteException
	 * @throws DatabaseReadException
	 */
	public boolean addTweet(Status tweet) throws DatabaseWriteException {

		JSONObject json = new JSONObject(tweet);
		int retweet = 0;
		boolean RT = false;

		LocalDateTime createdAt = tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		String created_at = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(createdAt);

		String text = tweet.getText().replaceAll("\r", " ").replaceAll("\n", " ");
		
		if (tweet.getRetweetedStatus() != null) {
			retweet = 1;
			RT = true;
		}

		PreparedStatement psmt_tweet = null;

		try {
			psmt_tweet = c.prepareStatement(addTweet);
			psmt_tweet.setLong(1, tweet.getId());
			psmt_tweet.setInt(2, id);

			psmt_tweet.setString(3, tweet.getUser().getScreenName());
			psmt_tweet.setString(4, created_at);
			psmt_tweet.setString(5, text);
			psmt_tweet.setInt(6, retweet);
			psmt_tweet.setString(7, json.toString());

			psmt_tweet.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error saving tweet " + tweet.getId(), e);
		}
		
		String textt = tweet.getText().replaceAll("\r", " ").replaceAll("\n", " ");

		DisplayableTweet t = new DisplayableTweet(tweet.getId(), created_at, tweet.getUser().getScreenName(),
				textt, RT);
		currentTweets.add(t);
		return true;
	}

	/**
	 * Check if a query has been searched previously or not for a specific users and
	 * returns its id
	 * 
	 * @param query
	 * @return
	 * @throws DatabaseReadException
	 */
	public Integer checkQuery(String query) throws DatabaseReadException {
		PreparedStatement psid = null;
		ResultSet rs = null;
		try {
			psid = c.prepareStatement(queryExists);
			psid.setInt(1, id);
			psid.setString(2, query);
			psid.setString(3, Main.getDBUserDAO().getUser());
			rs = psid.executeQuery();
			if (rs.next()) {
				return rs.getInt("collection_id");
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error searching the collection info.", e);
		}
		return null;
	}

	public boolean tweetExists(Status tweet) throws DatabaseReadException, DataNotFoundException {

		PreparedStatement psct = null;
		ResultSet rsct = null;
		try {
			psct = c.prepareStatement(checkTweet);
			psct.setLong(1, tweet.getId());
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

		retrieveTweets();
	}

	public void retrieveTweets() throws DatabaseReadException {

		if (!currentTweets.isEmpty()) {
			return;
		}

		boolean RT = false;

		PreparedStatement psut = null;
		ResultSet rst = null;
		try {
			psut = c.prepareStatement(retrieveTweets);
			psut.setInt(1, id);
			rst = psut.executeQuery();
			while (rst.next()) {
				if (rst.getInt("retweet") == 1) {
					RT = true;
				}

				String text = rst.getString("text_printable").replaceAll("\r", " ").replaceAll("\n", " ");
				
				DisplayableTweet t = new DisplayableTweet(rst.getLong("tweet_id"), rst.getString("created_at"),
						rst.getString("author"), text, RT);
				currentTweets.add(t);
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error reading the tweets info.", e);

		}

	}

	public long getNewestTweet() throws DatabaseReadException {
		PreparedStatement psnt = null;
		ResultSet rsnt = null;
		long tid = 0;
		try {
			psnt = c.prepareStatement(getNewestTweet);
			psnt.setInt(1, id);
			rsnt = psnt.executeQuery();
			tid = rsnt.getLong("max_tweet_id");
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error reading the tweets info.", e);
		}
		return tid;
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

	public ResultSet exportTweets() throws DatabaseReadException {
		PreparedStatement pstmt = null;
		ResultSet rsExp = null;
		try {
			pstmt = c.prepareStatement(exportCol);
			pstmt.setInt(1, id);

			rsExp = pstmt.executeQuery();

			return rsExp;
		} catch (SQLException e) {
			throw new DatabaseReadException("There was an error while exporting the tweets from the database.", e);
		}
	}

	public void printCSV(File file, ResultSet tweetsExp) throws DatabaseReadException {
		try {
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(file);
			CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL.withHeader("tweet_id", "created_at",
					"author", "text_printable", "url", "raw_tweet"));

			try {
				while (tweetsExp.next()) {
					String url = "https://twitter.com/" + tweetsExp.getString("author") + "/status/"
							+ tweetsExp.getInt("tweet_id");
					
					String text = tweetsExp.getString("text_printable").replaceAll("\r", " ").replaceAll("\n", " ");
					
					csvPrinter.printRecord(tweetsExp.getLong("tweet_id"), tweetsExp.getString("created_at"),
							tweetsExp.getString("author"), text, url,
							tweetsExp.getString("raw_tweet"));
				}
			} catch (SQLException e) {
				csvPrinter.flush();
				csvPrinter.close();
				throw new DatabaseReadException("There was an error while exporting the tweets from the database.", e);
			}

			csvPrinter.flush();
			csvPrinter.close();

		} catch (IOException ex) {
			Logger.getLogger(HistoricViewController.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}
