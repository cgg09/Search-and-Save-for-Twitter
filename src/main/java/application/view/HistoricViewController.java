package application.view;

import java.time.LocalDateTime;

import application.Main;
import application.database.DBCollection;
import application.exceptions.DatabaseReadException;
import application.utils.DisplayableTweet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class HistoricViewController extends AnchorPane {

	@FXML
	private TableView<DBCollection> historySearch;

	@FXML
	private TableColumn<DBCollection, LocalDateTime> dateColumn;
	@FXML
	private TableColumn<DBCollection, String> keywordColumn;
	private ObservableList<DBCollection> history = FXCollections.observableArrayList();
	
	@FXML
	private TableView<DisplayableTweet> currentSearch;
	@FXML
	private TableColumn<DisplayableTweet, LocalDateTime> createdAt;
	@FXML
	private TableColumn<DisplayableTweet, String> author;
	@FXML
	private TableColumn<DisplayableTweet, String> text;
	private ObservableList<DisplayableTweet> data = FXCollections.observableArrayList();	
	
	private static SearchViewController searchController;
	
	private DBCollection collection;

	private int from = 0;
	private int to;
	private int listSize = 0;
	

	public HistoricViewController() {

	}

	@FXML
	public void initialize() {
		
		// initialize historySearch
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
		keywordColumn.setCellValueFactory(cellData -> cellData.getValue().queryProperty());

		//initialize currentSearch
		createdAt.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
		author.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
		text.setCellValueFactory(cellData -> cellData.getValue().tweetTextProperty());

		// initialize user historySearch (sorted) 
		try {
			for(DBCollection dbc: Main.getDBUserDAO().retrieveCollections()) {
				history.add(dbc);
			}
		} catch (DatabaseReadException e) {
			e.printStackTrace();
		}

/*		
		SortedList<DBCollection> sortedHistory = new SortedList<>(history);
		
		sortedHistory.comparatorProperty().bind(historySearch.comparatorProperty());
		
		historySearch.setItems(sortedHistory);
*/
		historySearch.setItems(history);
		
		// update currentSearch from historySearch
		historySearch.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					try {
						newValue.updateCollection();
					} catch (DatabaseReadException e) {
						e.printStackTrace();
					} 
					addSearch(newValue);
				});
		
	}
	
	@FXML
	private void openWeb(MouseEvent event) {
		
		// see the selected tweet in a browser
		if(event.getClickCount() == 2) {
			System.out.println("Tweet selected: "+event.toString());
		}
	}
	
	@FXML
	private void handleNew() {

		collection = new DBCollection("Historic");

		boolean okClicked = searchController.newSearch(collection);
		if (okClicked && collection.getTweetStatus() != null) {
			addCollection();
			addSearch(collection);
		}
	}

	private void addCollection() {
		history.add(collection);
		historySearch.setItems(history);
	}

	private void addSearch(DBCollection collection) {
		
		this.collection = collection;
		
		if (!data.isEmpty()) {
			data.clear();
		}
		/*
		from = 0;
		
		listSize = collection.getCurrentTweets().size();

		from = Math.min(from, listSize);
		to = Math.min(from + 50, listSize);
		
		for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
			data.add(t);
		}*/
		
		data.addAll(collection.getCurrentTweets());

		SortedList<DisplayableTweet> sortedData = new SortedList<>(data);
		
		sortedData.comparatorProperty().bind(currentSearch.comparatorProperty());
		
		currentSearch.setItems(sortedData);
	}
/*
	@FXML
	private void nextTweets() {

		if (to == listSize) {
			System.out.println("Has llegado al final de la lista");
			return;
		}
		
		data.clear();
		
		from = Math.min(from + 50, listSize);
		to = Math.min(from + 50, listSize);
		
		for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
			data.add(t);
		}

		currentSearch.setItems(data);
	}

	@FXML
	private void previousTweets() {

		if (from == 0) { // INICIO DE LISTA: ensombrecer el botón para impedir el click !!
			System.out.println("Has llegado al inicio de la lista");
			return;
		}

		data.clear();
		
		to = Math.min(from, listSize );
		from = Math.max(to - 50, 0);
		
		for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
			data.add(t);
		}
		
		currentSearch.setItems(data);
	}
*/
	/*
	 * @FXML private void handleExport() { Database.exportCSV(search.getKeyword());
	 * }
	 */
	public static void init(SearchViewController controller) {
		searchController = controller;
	}

}
