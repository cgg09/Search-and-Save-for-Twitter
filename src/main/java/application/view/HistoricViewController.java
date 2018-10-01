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
import application.exceptions.AccessException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.exceptions.NetworkException;
import application.exceptions.RateLimitException;
import application.tasks.SearchTask;
import application.utils.DisplayableTweet;
import javafx.application.HostServices;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

/**
 * Controller of the historic mode of the application
 * @author Maria Cristina, github: cgg09
 *
 */

public class HistoricViewController extends AnchorPane {

	@FXML
	private Label username;
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

	@FXML
	private Button newSearch;
	
	private DBCollection collection;
	private String fm1 = "Last 200 tweets";
	private String fm2 = "All tweets (except RTs)";
	private String fm3 = "All tweets";
	
	private MenuItem h1 = new MenuItem("Repeat search...");
	private MenuItem h2 = new MenuItem("Save as...");
	private SeparatorMenuItem sp = new SeparatorMenuItem();
	private MenuItem h3 = new MenuItem("Delete search");
	
	/**
	 * The constructor, called before the initialize() method
	 */
	public HistoricViewController() {

	}
		
	/**
	 * Initializes the controller class This method is automatically called after
	 * the fxml file has been loaded
	 * 
	 * @throws DatabaseReadException
	 */
	@FXML
	public void initialize() {

		// initialize historySearch table
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
		keywordColumn.setCellValueFactory(cellData -> cellData.getValue().queryProperty());
		historySearch.setPlaceholder(new Label("No searches to display"));

		// initialize currentSearch table
		createdAt.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
		author.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
		text.setCellValueFactory(cellData -> cellData.getValue().tweetTextProperty());
		currentSearch.setPlaceholder(new Label("No tweets to display"));

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
		historyOptions.getItems().add(h1);
		historyOptions.getItems().add(h2);
		historyOptions.getItems().add(sp);
		historyOptions.getItems().add(h3);

		// initialize filter button options
		filterMenu.getItems().addAll(fm1, fm2, fm3);

		// update currentSearch from historySearch	
		historySearch.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(historySearch.getSelectionModel().selectedItemProperty().getValue() != null) {
				try {
					newValue.updateCollection();
				} catch (DatabaseReadException e) {
					e.printStackTrace();
				}
				filterMenu.setValue(fm1);
				addSearch(newValue);
			}		
		});

		// select options for each collection of the historySearch view
		historySearch.setRowFactory(tv -> {
			TableRow<DBCollection> row = new TableRow<>();
			row.setOnMouseEntered(e -> {
				if(!row.isEmpty()) {
					Main.getPrimaryStage().getScene().setCursor(Cursor.HAND);
				}
			});
			row.setOnMouseExited(e -> {
				if(!row.isEmpty()) {
					Main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
				}
			});
			row.setOnMouseClicked(event -> {
				if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
					DBCollection col = historySearch.getSelectionModel().getSelectedItem();
					h1.setOnAction(e -> {
						ProgressController progress = Main.showProgressBar("Downloading tweets");
						col.setRepeated(true);
						Task<Void> repeatSearchTask = new SearchTask(col,col.getQuery());
						handleRepeatSearch(col);
						/*Task<Boolean> repeatSearch = new Task<Boolean>() {

							@Override
							protected Boolean call() throws Exception {
								boolean d = handleRepeatSearch(col);
								return d;
							}

						};*/
						progress.getProcessStatus().textProperty().bind(repeatSearchTask.messageProperty());
						progress.getProgressBar().progressProperty().bind(repeatSearchTask.progressProperty());
						progress.getProcessStatus().textProperty().bind(repeatSearchTask.messageProperty());
						repeatSearchTask.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING,
								new EventHandler<WorkerStateEvent>() {
									@Override
									public void handle(WorkerStateEvent event) {
										int downloaded = col.getDownloaded();
										progress.getProcessStatus().textProperty().unbind();
										progress.getProcessStatus().setText("Downloaded tweets: " + downloaded);

									}
								});
						repeatSearchTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
								new EventHandler<WorkerStateEvent>() {
									@Override
									public void handle(WorkerStateEvent event) {
										progress.getStage().close();
										Alert alert = new Alert(AlertType.INFORMATION);
										alert.setTitle("NEW SEARCH FINISHED");
										alert.setHeaderText("Tweets downloaded");
										if (col.getDownloaded() != 0) {
											alert.setContentText(
													"Success! You have downloaded " + col.getDownloaded() + " tweets");
										} else {
											alert.setContentText("You haven't downloaded any tweet, try next time!");
										}
										alert.showAndWait();
										
									}
								});
						new Thread(repeatSearchTask).start();
					});
					h2.setOnAction(e -> {
						try {
							handleExport(col);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
					h3.setOnAction(e -> handleDelete(col));
					historySearch.setContextMenu(historyOptions);
				}
			});
			return row;
		});

		// change of selection in filter button "show"
		filterMenu.getSelectionModel().selectedIndexProperty()
				.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
					filterFunction(filterMenu.getItems().get((Integer) newValue));
				});
		
		// show a selected tweet in a browser
		currentSearch.setRowFactory(cRow -> {
			TableRow<DisplayableTweet> currentRow = new TableRow<>();
			currentRow.setOnMouseEntered(e -> {
				if(!currentRow.isEmpty()) {
					Main.getPrimaryStage().getScene().setCursor(Cursor.HAND);
				}
			});
			currentRow.setOnMouseExited(e -> {
				if(!currentRow.isEmpty()) {
					Main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
				}
			});
			currentRow.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !currentRow.isEmpty()) {
					DisplayableTweet rowTweet = currentRow.getItem();
					HostServices host = Main.getInstance().getHostServices();
					host.showDocument("https://twitter.com/" + rowTweet.getAuthor() + "/status/" + rowTweet.getId());
				}
			});
			return currentRow;
		});
		
		// open new search window
		newSearch.setOnKeyPressed(event->{
			if(event.getCode() == KeyCode.ENTER) {
				handleNewSearch();
			}
		});
	}

	@FXML
	private void handleNewSearch() {
		collection = new DBCollection("Historic");
		searchController.newSearch(collection,this);		
	}

	public void updateViews() {
		if (collection.getCurrentTweets() != null) {
			if (!collection.getRepeated()) {
				System.out.println("Hey");
				history.add(collection);
			}
			Comparator<DBCollection> collectionComparator = Comparator.comparing(DBCollection::getStart);
			FXCollections.sort(history, collectionComparator.reversed());
			historySearch.setItems(history);
			addSearch(collection);
		}
	}

	private void addSearch(DBCollection c) {
		collection = c;

		if (!data.isEmpty()) {
			data.clear();
		}

		int to = Math.min(200, collection.getCurrentTweets().size());

		data.addAll(collection.getCurrentTweets().subList(0, to));

		Comparator<DisplayableTweet> tweetComparator = Comparator.comparing(DisplayableTweet::getCreatedAt);
		FXCollections.sort(data, tweetComparator.reversed());

		currentSearch.setItems(data);
		int listSize = collection.getCurrentTweets().size();
		filterMenu.getItems().set(2, "All tweets (" + listSize + ")"); // FIXME I am not sure if the # of total tweets is updated correctly
	}

	/**
	 * Sort collections in correct order in case of repeating a search
	 * @param c (collection)
	 */
	private void handleRepeatSearch(DBCollection c) {
		// FIXME I am not sure that this works correctly
		System.out.println("Sorting collections by reverse date");
		Comparator<DBCollection> collectionComparator = Comparator.comparing(DBCollection::getStart);
		FXCollections.sort(history, collectionComparator.reversed());

		System.out.println("Re-drawing historySearch");
		historySearch.setItems(history);
		addSearch(c);

	}

	/**
	 * General export collection button from the bottom right of the search view
	 */
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

	/**
	 * General method to export a collection
	 * @param c (collection)
	 * @throws IOException
	 */
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
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV file (delimited with semicolons)", ".csv"));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV file (delimited with commas)", ".csv"));
		File file = fileChooser.showSaveDialog(Main.getPrimaryStage());

		if (file != null) {
			String[] description = fileChooser.getSelectedExtensionFilter().getDescription().split(" ");
			String delimiter = description[description.length-1].replaceAll("\\)", "").replaceAll(" ", "");
			
			try {
				c.printCSV(file, tweetsExp, delimiter);
			} catch (DatabaseReadException e) {
				e.printStackTrace();
			}

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("COLLECTION EXPORTED");
			alert.setHeaderText("Export Collection");
			alert.setContentText("Congratulations! You have exported succesfully the collection \"" + c.getQuery()
					+ "\". Do you want to open the file?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				Desktop.getDesktop().open(file);
			}
		}
	}

	/**
	 * Delete collection method for the same history table option
	 * @param c (collection)
	 */
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

	/**
	 * Method for the top right menu in which the user can filter the tweets shown in the current view
	 * @param option
	 */
	public void filterFunction(String option) {
		data.clear();
		if (option == fm1) {
			System.out.println("Option: "+option+". Menu: "+fm1);
			int to = Math.min(200, collection.getCurrentTweets().size());
			data.addAll(collection.getCurrentTweets().subList(0, to));
		} else if (option == fm2) {
			System.out.println("Option: "+option+". Menu: "+fm2);
			for (DisplayableTweet t : collection.getCurrentTweets()) {
				if (!t.getRetweet()) {
					data.add(t);
				}
			}
		} else {
			System.out.println("Option: "+option+". Menu: "+fm3);
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
