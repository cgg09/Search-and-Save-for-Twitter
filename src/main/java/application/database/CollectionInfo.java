package application.database;

import java.sql.Connection;

public class CollectionInfo { // hipotética clase ...

	
	private int id;
	
	private Connection connection;
	private static String className = "org.sqlite.JDBC";
	private static String databasePath;
	private static String user; 
	
	private String addCollection;
	//private String addTweet = "INSERT INTO TWEET (TWEET_ID, COLLECTION_ID, AUTHOR, CREATED_AT, TEXT_PRINTABLE) VALUES (?,?,?,?,?);"; // CÓMO hacer esto!!! ??¿¿
	private String addUser;
	
	private String getCollection = "";
	private String getTweet = "";
	private String getUserData = "";
	
	
	public CollectionInfo() {
		addCollection = "INSERT INTO COLLECTION (USERNAME, TIME_START, TIME_END, TYPE, QUERY) VALUES (?,?,?,?,?);";
		addUser = "INSERT INTO USER (USERNAME, ACCESS_TOKEN, ACCESS_SECRET) VALUES (?,?,?);";
	}
	
	
}
