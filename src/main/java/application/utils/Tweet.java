package application.utils;

import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Tweet {

	private ObjectProperty<LocalDateTime> createdAt;
	private StringProperty author;
	private StringProperty text;
	
	public Tweet(LocalDateTime c, String a, String t) {
		this.createdAt = new SimpleObjectProperty<LocalDateTime>(c);
		this.author = new SimpleStringProperty(a);
		this.text = new SimpleStringProperty(t);
	}
	
	public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
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
}
