package application.view;

import java.sql.Timestamp;
import java.util.Optional;

import application.Main;
import application.database.DBCollection;
import application.exceptions.ConnectivityException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class NewHistoricDialogController {

	Twitter twitter;

	@FXML
	private Label downloadedTweets = new Label("");
	@FXML
	private TextField userQuery;
	@FXML
	private Button searchButton;
	private DBCollection collection;
	private Stage dialogStage;
	private boolean okClicked;
	int total;
	boolean repeat = false;

	public NewHistoricDialogController() {

	}

	public void initialize() {
		
		searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				//System.out.println("Button: "+searchButton.getText());
				dialogStage.getScene().setCursor(Cursor.WAIT);
				//searchButton.setText("Searching...");	// FIXME la 1a vez cambia, pero la segunda no...
				// downloadedTweets.setText("Downloaded tweets: "+total); // FIXME de momento no hace caso... :(
				
				Task<Void> task1 = new Task<Void>() {
					@Override
					public Void call() {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									handleSearch();
								} catch (ConnectivityException | DatabaseReadException e) {
									e.printStackTrace();
								}
							}
						});
						return null;
					}
				};
				new Thread(task1).start();
				//System.out.println("Button: "+searchButton.getText());
			}

		});

	}

	public void setDialogStage(Stage stage) {
		dialogStage = stage;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	public void setCollection(DBCollection c) {
		this.collection = c;
		if(!collection.getQuery().isEmpty()) {
			System.out.println("Not empty: "+collection.getQuery());
			userQuery.setText(collection.getQuery());
		}
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	public void handleSearch() throws ConnectivityException, DatabaseReadException { // FIXME throws RateLimitException, DatabaseReadException

		total = 0;

		if (!collection.getTweetStatus().isEmpty()) {
			collection.getTweetStatus().clear();
		}

		Query query = new Query();
		QueryResult queryResult = null;
		
		Integer col = collection.checkQuery(userQuery.getText());
		if(col!=null){		
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("REPEAT SEARCH");
			alert.setHeaderText("Repeating search");
			alert.setContentText("Wait! This query is for a previous search. Do you want to add the tweets downloaded to the old search ?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				repeat = true;
				collection.setId(col);

				try {
					collection.updateCollection();
				} catch (DatabaseReadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				collection.setQuery(userQuery.getText());
			}
		} else {
			//DBCollection c = new DBCollection("Historic");
			//collection = c;
			collection.setQuery(userQuery.getText());
		}

		query.setQuery(collection.getQuery());
		long tid = collection.getNewestTweet();
		
		query.sinceId(tid);

		System.out.println("Searching...");

		Timestamp ts_start = new Timestamp(System.currentTimeMillis());
		do {

			try {
				queryResult = twitter.search(query);
				//twitter.search(query).getSinceId();
			} catch (TwitterException e) {
				if(400 == e.getStatusCode()) {
					e.printStackTrace(); // TODO The request was invalid: query parameters are missing
				} else {
					throw new ConnectivityException("You do not have internet connection. Please check it out before continue",e);
				}
			}
			collection.saveTweetStatus(queryResult);
			total += queryResult.getCount();
			// TODO twitter.addRateLimitStatusListener(e -> throw new RateLimitException());
		} while ((query = queryResult.nextQuery()) != null && total <= 430); // FIXME 

		Timestamp ts_end = new Timestamp(System.currentTimeMillis());		
		if(!repeat) {
			collection.addData(ts_start, ts_end, Main.getDBUserDAO());
		} else {
			try {
				collection.retrieveTweets();
			} catch (DatabaseReadException e1) {
				e1.printStackTrace();
			}
			for (Status tweet : collection.getTweetStatus()) {
				try {
					collection.addTweet(tweet);
				} catch (DatabaseWriteException e) {
					e.printStackTrace();
				}
			}
			
			collection.getCurrentTweets().clear();
			try {
				collection.retrieveTweets();
			} catch (DatabaseReadException e) {
				e.printStackTrace();
			}
			
			System.out.println(collection.getCurrentTweets().size());
		}

		okClicked = true;

		dialogStage.close();
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("NEW SEARCH FINISHED");
		alert.setHeaderText("Tweets downloaded");
		alert.setContentText("Well done! You have downloaded succesfully "+total+" tweets");
		alert.showAndWait();
		
//		searchButton.setText("Search");

	}

	@FXML
	private void handleCancel() {
		collection = null;
		dialogStage.close();
	}

	public void closeStage() {
		dialogStage.close();
	}
	
	public boolean repeatSearch() {
		return repeat;
	}
}
