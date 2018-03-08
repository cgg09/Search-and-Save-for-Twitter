package application.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import twitter4j.JSONObject;
import twitter4j.Status;

public class TwitterSearch {
	
	private final StringProperty keyword;
	private List<Status> tweets = null;
	
	public TwitterSearch() {
		this(null);
	}
	
	public TwitterSearch(String keyword) {
		this.keyword = new SimpleStringProperty(keyword);
//		this.tweets; = 
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
	
	public void addTweets(List<Status> tweets) {
		this.tweets = tweets;
	}
	
}
