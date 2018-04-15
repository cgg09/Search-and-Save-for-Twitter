package application.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Vector;

import application.Main;
import application.database.DBCollection;
import application.utils.DisplayableTweet;
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
	
	int listSize = 0;
	
	private Main main;

	public HistoricViewController() {

	}

	@FXML
	public void initialize() {
		System.out.println("Initializing Historic VC");
		
		// initialize historySearch
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
		keywordColumn.setCellValueFactory(cellData -> cellData.getValue().queryProperty());

		//initialize currentSearch
		createdAt.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
		author.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
		text.setCellValueFactory(cellData -> cellData.getValue().tweetTextProperty());

		updateTable();

		// update currentSearch from historySearch
		historySearch.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {newValue.updateCollection(); addSearch(newValue);});

		//historySearch.setItems(history);
	}
	
	public void updateTable() {
		// initialize user historySearch
		List<DBCollection> cols = new Vector<DBCollection>();
		//cols = 
		for(DBCollection dbc: Main.getDBUserDAO().retrieveCollections()) {
			history.add(dbc);
		}
		System.out.println("Is empty? "+history.isEmpty());
		historySearch.setItems(history);
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
		
		listSize = collection.getCurrentTweets().size();

		from = Math.min(from, listSize);
		to = Math.min(from + 50, listSize);
		
		for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
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

		to = Math.min(from, listSize );
		from = Math.max(to - 50, 0);

		data.clear();
		
		for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
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
