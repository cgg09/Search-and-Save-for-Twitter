package application.model;

import java.time.LocalDate;

import application.database.DBCollection;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HistoricSearch extends TwitterSearch {

	private DBCollection collection;
	private final ObjectProperty<LocalDate> date;
	private StringProperty snippet;
	
	
	public HistoricSearch(DBCollection collection) {
		super();
		this.collection = collection;
		this.date = new SimpleObjectProperty<LocalDate>();
		this.snippet = new SimpleStringProperty(query.get());
	}
	
	public DBCollection getCollection(){
		return collection;
	}
	
	public LocalDate getDate() {
		return date.get();
	}
	
	public void setDate(LocalDate date) {
		this.date.set(date);
	}
	
	public ObjectProperty<LocalDate> dateProperty() {
		return date;
	}
	
	public String getSnippet() {
		return snippet.get();
	}
	
	public StringProperty snippetProperty() {
		return snippet;
	}
	
}
