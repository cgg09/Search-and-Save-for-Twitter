package application.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class HistoricSearch extends TwitterSearch {

	private final ObjectProperty<LocalDate> date;
	
	public HistoricSearch() {
		super();
		this.date = new SimpleObjectProperty<LocalDate>();
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
	
}
