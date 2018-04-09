package application.model;

import java.util.List;
import java.util.Vector;

import application.database.DBCollection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.QueryResult;
import twitter4j.Status;

public class TwitterSearch {
	
	protected DBCollection collection;
	protected StringProperty query = new SimpleStringProperty("");
	private List<Status> tweets = new Vector<Status>();
	
	public TwitterSearch() {

	}
	
	public DBCollection getCollection(){
		return collection;
	}
	
	public void deleteCollection(){
		collection = null;
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
	
	public List<Status> getTweetList(){
		return tweets;
	}
		
	public void addTweets(QueryResult tweetList) {
		this.tweets.addAll(tweetList.getTweets());
	}	
	
}
