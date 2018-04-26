package application.view;

import java.time.LocalDateTime;
import java.util.Optional;

import application.Main;
import application.database.DBCollection;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.utils.DisplayableTweet;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class HistoricViewController extends AnchorPane {

	@FXML
	private ChoiceBox<String> filterMenu = new ChoiceBox<String>();

	@FXML
	private TableView<DBCollection> historySearch;
	@FXML
	private TableColumn<DBCollection, LocalDateTime> dateColumn;
	@FXML
	private TableColumn<DBCollection, String> keywordColumn;
	private ObservableList<DBCollection> history = FXCollections.observableArrayList();

	private ContextMenu historyOptions = new ContextMenu();

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

	/*
	 * private int from = 0; private int to; private int listSize = 0;
	 */

	public HistoricViewController() {

	}

	@FXML
	public void initialize() {

		// initialize historySearch
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
		keywordColumn.setCellValueFactory(cellData -> cellData.getValue().queryProperty());

		// initialize currentSearch
		createdAt.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
		author.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
		text.setCellValueFactory(cellData -> cellData.getValue().tweetTextProperty());

		// initialize user historySearch
		try {
			for (DBCollection dbc : Main.getDBUserDAO().retrieveCollections()) {
				history.add(dbc);
			}
		} catch (DatabaseReadException e) {
			e.printStackTrace();
		}

		historySearch.setItems(history);

		historySearch.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// initialize historicSearch options for each collection
		MenuItem m1 = new MenuItem("Export collection");
		MenuItem m2 = new MenuItem("Delete collection");
		historyOptions.getItems().add(m1);
		historyOptions.getItems().add(m2);

		// initialize filter button to "last 200 tweets"
		filterMenu.getItems().addAll("Last 200 tweets", "All tweets (except RTs)", "All tweets");
		
		// update currentSearch from historySearch
		historySearch.setRowFactory( tv -> {
		    TableRow<DBCollection> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	DBCollection rowData = row.getItem();
		            try {
						rowData.updateCollection();
					} catch (DatabaseReadException e) {
						e.printStackTrace();
					}
		            filterMenu.setValue("Last 200 tweets");
					addSearch(rowData);
		        }
		    });
		    return row ;
		});

		// opciones al seleccionar con click derecho una collection
		historySearch.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent m) {
				if (m.getButton() == MouseButton.SECONDARY) { // FIXME the menu is shown in an empty row !!
					DBCollection col = historySearch.getSelectionModel().getSelectedItem();
					m1.setOnAction(e -> handleExport(col));
					m2.setOnAction(e -> handleDelete(col));
					historyOptions.show(historySearch, m.getScreenX(), m.getScreenY());

				}

			}

		});
		
		// change of selection in filter button "show"
		filterMenu.getSelectionModel().selectedIndexProperty()
				.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
					System.out.println("You clicked #"+newValue+": "+filterMenu.getItems().get((Integer) newValue));
					filterFunction((Integer)newValue);
				});


	}

	@FXML
	private void openWeb(MouseEvent event) {

		// TODO see the selected tweet in a browser
		if (event.getClickCount() == 2) {
			System.out.println("Tweet selected: " + event.toString());
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

	private void addSearch(DBCollection c) {

		collection = c;

		if (!data.isEmpty()) {
			data.clear();
		}
		/*
		 * from = 0;
		 * 
		 * listSize = collection.getCurrentTweets().size();
		 * 
		 * from = Math.min(from, listSize); to = Math.min(from + 50, listSize);
		 * 
		 * for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
		 * data.add(t); }
		 */

		int to = Math.min(200, collection.getCurrentTweets().size());

		data.addAll(collection.getCurrentTweets().subList(0, to));

		// SortedList<DisplayableTweet> sortedData = new SortedList<>(data);

		// sortedData.comparatorProperty().bind(currentSearch.comparatorProperty()); //
		// no se si esto de aquí funciona o no, diría que no

		currentSearch.setItems(data);
	}

	private void handleExport(DBCollection c) { // Database.exportCSV(search.getKeyword());
		System.out.println("Collection to export: " + c);
	}

	private void handleDelete(DBCollection c) {
	
		Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("DELETE COLLECTION");
        alert.setHeaderText("Delete Collection");
        alert.setContentText("Are you sure you want to delete the collection \""+c.getQuery()+"\"?");
        
        Optional<ButtonType> result = alert.showAndWait(); 
        if(result.get() == ButtonType.OK) {
        	try {
    			c.deleteCollection();
    		} catch (DatabaseWriteException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        	int selectedIndex = historySearch.getSelectionModel().getSelectedIndex();
        	historySearch.getItems().remove(selectedIndex);
        	if(!data.isEmpty()) {
        		data.clear();
        	}
        	c = null;
        }

	}

	public void filterFunction(int number) { //FIXME filter works! I just need to check things on it :o
		/*if (data.isEmpty()) {
			System.out.println("Meh :(");
			return;
		}*/ // por ahora, ni idea de si esto sirve para algo
		System.out.println("Buu");
		data.clear();
		if (number == 0) { // last 200 tweets (default)
			System.out.println("Hi");
			int to = Math.min(200, collection.getCurrentTweets().size());
			data.addAll(collection.getCurrentTweets().subList(0, to));
		} else if (number == 1) { // all non-RT tweets
			System.out.println("Hii");
			for (DisplayableTweet t : collection.getCurrentTweets()) {
				if (!t.getRetweet()) {
					data.add(t);
				}
			}
		} else if (number == 2) { // all tweets
			System.out.println("Hiii");
			data.addAll(collection.getCurrentTweets());
		}
		currentSearch.setItems(data);
	}

	public static void init(SearchViewController controller) {
		searchController = controller;
	}

	/*
	 * @FXML private void nextTweets() {
	 * 
	 * if (to == listSize) { System.out.println("Has llegado al final de la lista");
	 * return; }
	 * 
	 * data.clear();
	 * 
	 * from = Math.min(from + 50, listSize); to = Math.min(from + 50, listSize);
	 * 
	 * for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
	 * data.add(t); }
	 * 
	 * currentSearch.setItems(data); }
	 * 
	 * @FXML private void previousTweets() {
	 * 
	 * if (from == 0) { // INICIO DE LISTA: ensombrecer el botón para impedir el
	 * click !! System.out.println("Has llegado al inicio de la lista"); return; }
	 * 
	 * data.clear();
	 * 
	 * to = Math.min(from, listSize ); from = Math.max(to - 50, 0);
	 * 
	 * for(DisplayableTweet t : collection.getCurrentTweets().subList(from, to)) {
	 * data.add(t); }
	 * 
	 * currentSearch.setItems(data); }
	 */

}
