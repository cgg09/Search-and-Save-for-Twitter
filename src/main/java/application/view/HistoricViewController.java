package application.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.database.DBCollection;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.utils.DisplayableTweet;
import javafx.application.HostServices;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class HistoricViewController extends AnchorPane {

	@FXML
	private ChoiceBox<String> filterMenu = new ChoiceBox<String>();

	@FXML
	private TableView<DBCollection> historySearch;
	@FXML
	private TableColumn<DBCollection, String> dateColumn;
	@FXML
	private TableColumn<DBCollection, String> keywordColumn;
	private ObservableList<DBCollection> history = FXCollections.observableArrayList();

	private ContextMenu historyOptions = new ContextMenu();

	@FXML
	private TableView<DisplayableTweet> currentSearch;
	@FXML
	private TableColumn<DisplayableTweet, String> createdAt;
	@FXML
	private TableColumn<DisplayableTweet, String> author;
	@FXML
	private TableColumn<DisplayableTweet, String> text;
	private ObservableList<DisplayableTweet> data = FXCollections.observableArrayList();

	private static SearchViewController searchController;

	private DBCollection collection;

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

		// initialize historicSearch options for each collection //FIXME add repeat search
		MenuItem m1 = new MenuItem("Export collection");
		MenuItem m2 = new MenuItem("Delete collection");
		historyOptions.getItems().add(m1);
		historyOptions.getItems().add(m2);

		// initialize filter button to "last 200 tweets"
		filterMenu.getItems().addAll("Last 200 tweets", "All tweets (except RTs)", "All tweets");

		
		// update currentSearch from historySearch && select options for each collection of the historySearch view
		historySearch.setRowFactory(tv -> {
			TableRow<DBCollection> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getButton() == MouseButton.PRIMARY && (!row.isEmpty())) {
					DBCollection rowData = row.getItem();
					try {
						rowData.updateCollection();
					} catch (DatabaseReadException e) {
						e.printStackTrace();
					}
					filterMenu.setValue("Last 200 tweets");
					addSearch(rowData);
				} 
				else if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
					DBCollection col = historySearch.getSelectionModel().getSelectedItem();
					m1.setOnAction(e -> handleExport(col));
					m2.setOnAction(e -> handleDelete(col));
					historySearch.setContextMenu(historyOptions);
				}
			});
			return row;
		});
		
		// change of selection in filter button "show"
		filterMenu.getSelectionModel().selectedIndexProperty()
				.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
					filterFunction((Integer) newValue);
				});
		
		// show a selected tweet in a browser
		currentSearch.setRowFactory(cRow -> {
			TableRow<DisplayableTweet> currentRow = new TableRow<>();
			currentRow.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !currentRow.isEmpty()) {
					DisplayableTweet rowTweet = currentRow.getItem();
					HostServices host = searchController.getMain().getHostServices();
					host.showDocument("https://twitter.com/"+rowTweet.getAuthor()+"/status/"+rowTweet.getId());
				}
			});
			return currentRow;
		});
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

		int to = Math.min(200, collection.getCurrentTweets().size());

		data.addAll(collection.getCurrentTweets().subList(0, to));

		currentSearch.setItems(data);
		int listSize = collection.getCurrentTweets().size();
		filterMenu.getItems().set(2, "All tweets ("+listSize+")");
	}

	private void handleExport(DBCollection c) {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd"));
		String filename = date + "_" + c.getQuery() + ".csv";

		String tweets = "";
		try {
			tweets = c.exportTweets();	// FIXME iterador ; apache commons csv ; printRecords(ResultSet rs) !!!
		} catch (DatabaseReadException e) {
			e.printStackTrace();
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save as");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setInitialFileName(filename);
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV file", ".csv"));
		File file = fileChooser.showSaveDialog(Main.getPrimaryStage());
		if (file != null) {
			try {
				FileWriter fileWriter = null;
				fileWriter = new FileWriter(file);
				fileWriter.write(tweets);
				fileWriter.flush();
				fileWriter.close();

			} catch (IOException ex) {
				Logger.getLogger(HistoricViewController.class.getName()).log(Level.SEVERE, null, ex);
			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("COLLECTION EXPORTED");
			alert.setHeaderText("Export Collection");
			alert.setContentText("Well done! You have exported succesfully the collection \"" + c.getQuery() + "\". ");
			alert.showAndWait();
		}
	}

	private void handleDelete(DBCollection c) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("DELETE COLLECTION");
		alert.setHeaderText("Delete Collection");
		alert.setContentText("Are you sure you want to delete the collection \"" + c.getQuery() + "\"?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				c.deleteCollection();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}

			int selectedIndex = historySearch.getSelectionModel().getSelectedIndex();
			historySearch.getItems().remove(selectedIndex);
			if (!data.isEmpty()) {
				data.clear();
			}
			c = null;
		}
	}

	public void filterFunction(int number) { // FIXME filter works! I just need to check things on it :o
		data.clear();
		if (number == 0) { // last 200 tweets (default)
			int to = Math.min(200, collection.getCurrentTweets().size());
			data.addAll(collection.getCurrentTweets().subList(0, to));
		} else if (number == 1) { // all non-RT tweets
			for (DisplayableTweet t : collection.getCurrentTweets()) {
				if (!t.getRetweet()) {
					data.add(t);
				}
			}
		} else if (number == 2) { // all tweets
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
	 * if (from == 0) { // INICIO DE LISTA: ensombrecer el bot�n para impedir el
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
