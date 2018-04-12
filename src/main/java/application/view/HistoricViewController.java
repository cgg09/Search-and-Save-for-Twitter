package application.view;

import java.time.LocalDateTime;
import java.util.Collections;

import application.database.DBCollection;
import application.utils.Tweet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
	private TableView<Tweet> currentSearch;
	@FXML
	private TableColumn<Tweet, LocalDateTime> createdAt;
	@FXML
	private TableColumn<Tweet, String> author;
	@FXML
	private TableColumn<Tweet, String> text;
	private ObservableList<Tweet> data = FXCollections.observableArrayList();
	
	
	private static SearchViewController searchController;
	private DBCollection collection;
	
	//private TwitterSearch search;

	private int from = 0;

	private int to;
	
	int listSize = 0;

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

		// update currentSearch from historySearch
		historySearch.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {newValue.updateCollection(); addSearch(newValue);});
	}
	
	@FXML
	private void handleNew() {

		collection = new DBCollection("Historic");

		boolean okClicked = searchController.newSearch(collection);
		if (okClicked && collection.getTweetList() != null) {
			addCollection();
			addSearch(collection); // cambiar para que entre collections
		}
	}

	private void addCollection() {
		history.add(collection);
		historySearch.setItems(history);
	}

	private void addSearch(DBCollection collection) {

		System.out.println("Current search: "+collection.getId());
		
		if (!data.isEmpty()) {
			data.clear();
			System.out.print("Data cleaned\n");
		}
		
		// ordenar por fecha
		
		listSize = collection.getCurrentTweets().size();

		from = Math.min(from, listSize);
		to = Math.min(from + 50, listSize);
		
		for(Tweet t : collection.getCurrentTweets().subList(from, to)) {
			data.add(t);
		}

		currentSearch.setItems(data);
	}

	@FXML
	private void nextTweets() {

		if (to == listSize) {
			System.out.println("Has llegado al final de la lista");
			return;
		}
		from = Math.min(from + 50, listSize);
		to = Math.min(from + 50, listSize);

		data.clear();
		
		for(Tweet t : collection.getCurrentTweets().subList(from, to)) {
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

		to = Math.min(from, listSize );
		from = Math.max(to - 50, 0);

		data.clear();
		
		for(Tweet t : collection.getCurrentTweets().subList(from, to)) {
			data.add(t);
		}
		
		currentSearch.setItems(data);
	}

	/*
	 * añadir método para seleccionar una búsqueda y mostrarla en la vista de tweets
	 */

	/*
	 * @FXML private void handleExport() { Database.exportCSV(search.getKeyword());
	 * }
	 */
	public static void init(SearchViewController controller) {
		searchController = controller;

	}

}
