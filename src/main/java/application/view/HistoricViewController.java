package application.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

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
import javafx.scene.control.ButtonBar.ButtonData;
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

	int total;

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
		MenuItem m1 = new MenuItem("Repeat search");
		MenuItem m2 = new MenuItem("Export collection");
		MenuItem m3 = new MenuItem("Delete collection");
		historyOptions.getItems().add(m1);
		historyOptions.getItems().add(m2);
		historyOptions.getItems().add(m3);

		// initialize filter button options
		filterMenu.getItems().addAll("Last 200 tweets", "All tweets (except RTs)", "All tweets");

		// update currentSearch && select options for each collection of the
		// historySearch view
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
				} else if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
					DBCollection col = historySearch.getSelectionModel().getSelectedItem();
					m1.setOnAction(e -> handleRepeatSearch(col));
					m2.setOnAction(e -> {
						try {
							handleExport(col);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
					m3.setOnAction(e -> handleDelete(col));
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
					host.showDocument("https://twitter.com/" + rowTweet.getAuthor() + "/status/" + rowTweet.getId());
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
			if (!collection.getRepeated()) {
				addCollection();
			}
			addSearch(collection);
		}
	}

	private void addCollection() {

		history.add(collection);
		Comparator<DBCollection> comparator = Comparator.comparing(DBCollection::getStart);
		FXCollections.sort(history, comparator.reversed());
		historySearch.setItems(history);
	}

	private void addSearch(DBCollection c) {
		collection = c;

		if (!data.isEmpty()) {
			data.clear();
		}

		int to = Math.min(200, collection.getCurrentTweets().size());

		data.addAll(collection.getCurrentTweets().subList(0, to));

		Comparator<DisplayableTweet> comparator = Comparator.comparing(DisplayableTweet::getCreatedAt);
		FXCollections.sort(data, comparator.reversed());

		currentSearch.setItems(data);
		int listSize = collection.getCurrentTweets().size();
		filterMenu.getItems().set(2, "All tweets (" + listSize + ")");
	}
	
	/*
	 * Methods of the history options menu:
	 */

	private void handleRepeatSearch(DBCollection c) {

		boolean okClicked = searchController.newSearch(c);
		if (okClicked && c.getTweetStatus() != null) {
			if (!c.getRepeated()) {
				addCollection();
			}
			addSearch(c);
		}
	}

	@FXML
	private void manageExport() {
		if (!data.isEmpty()) {
			DBCollection c = historySearch.getSelectionModel().getSelectedItem();
			if (c.getCurrentTweets().get(0) == data.get(0)) { // FIXME look for a better solution :-/
				try {
					handleExport(c);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void handleExport(DBCollection c) throws IOException {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd"));
		String filename = date + "_" + c.getQuery() + ".csv";

		ResultSet tweetsExp = null;
		try {
			tweetsExp = c.exportTweets();
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
				c.printCSV(file, tweetsExp);
			} catch (DatabaseReadException e) {
				e.printStackTrace();
			}
		
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("COLLECTION EXPORTED");
			alert.setHeaderText("Export Collection");
			alert.setContentText("Well done! You have exported succesfully the collection \"" + c.getQuery()
					+ "\". Do you want to open the file?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				Desktop.getDesktop().open(file);
			}
		}
	}

	private void handleDelete(DBCollection c) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("DELETE COLLECTION");
		alert.setHeaderText("Delete Collection");
		alert.setContentText("Are you sure you want to delete the collection \"" + c.getQuery() + "\"?");

		ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOk) {
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

	public void filterFunction(int number) {
		//FIXME not by number !!!
		data.clear();
		if (number == 0) {
			int to = Math.min(200, collection.getCurrentTweets().size());
			data.addAll(collection.getCurrentTweets().subList(0, to));
		} else if (number == 1) {
			for (DisplayableTweet t : collection.getCurrentTweets()) {
				if (!t.getRetweet()) {
					data.add(t);
				}
			}
		} else if (number == 2) {
			data.addAll(collection.getCurrentTweets());
		}

		Comparator<DisplayableTweet> comparator = Comparator.comparing(DisplayableTweet::getCreatedAt);
		FXCollections.sort(data, comparator.reversed());

		currentSearch.setItems(data);
	}

	public static void init(SearchViewController controller) {
		searchController = controller;
	}

}
