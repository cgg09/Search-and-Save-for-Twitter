package application.model;

import java.util.List;
import java.util.Vector;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.QueryResult;
import twitter4j.Status;

public class TwitterSearch {
	
	
	protected final StringProperty keyword;
	private List<Status> tweets = new Vector<Status>();
	
	public TwitterSearch() {
		this(null);
	}
	
	public TwitterSearch(String keyword) {
		this.keyword = new SimpleStringProperty(keyword);
		
	}
	
	public String getKeyword() {
		return keyword.get();
	}
	
	public void setKeyword(String keyword) {
		this.keyword.set(keyword);
	}
	
	public StringProperty keywordProperty() {
		return keyword;
	}
/*
	public ObservableList<JSONObject> getTweetList(){
		return tweets;
	}
*/	
	public List<Status> getTweetList(){
		return tweets;
	}
	

	
	public void addTweets(QueryResult tweetList) {

		this.tweets.addAll(tweetList.getTweets());
//		for(Status t : tweetList.getTweets()) {
//			this.tweets.add(t);
//		}
		
		
	}	
	
}
