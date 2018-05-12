package application.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DisplayableTweet {

	private final long tweet_id;
	private StringProperty createdAt;
	private StringProperty author;
	private StringProperty text;
	private boolean retweet;
	
	public DisplayableTweet(long tweet_id, String c, String a, String t, boolean r) {
		this.tweet_id = tweet_id;
		this.createdAt = new SimpleStringProperty(c);
		this.author = new SimpleStringProperty(a);
		//FIXME saltos de línea en el texto
		this.text = new SimpleStringProperty(t);
		this.retweet = r;
	}
	
	public long getId() {
		return tweet_id;
	}
	
	public String getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt.set(createdAt);
    }

    public StringProperty createdAtProperty() {
        return createdAt;
    }
    
    public String getAuthor() {
		return author.get();
	}
	
	public void setAuthor(String author) {
		this.author.set(author);
	}
	
	public StringProperty authorProperty() {
		return author;
	}
	
	public String getTweetText() {
		return text.get();
	}
	
	public void setTweetText(String text) {
		this.text.set(text);
	}
	
	public StringProperty tweetTextProperty() {
		return text;
	}
	
	public boolean getRetweet() {
		return retweet;
	}
	
	public void setRetweet(boolean retweet) {
		this.retweet = retweet;
	}
}
