package application.view;

import java.sql.Timestamp;

import application.database.DBCollection;
import application.exceptions.ConnectivityException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class NewHistoricDialogController {

	Twitter twitter;

	@FXML
	private Label downloadedTweets = new Label("");
	@FXML
	private TextField userQuery;
	@FXML
	private Button searchButton;// = new Button("Search");
	private DBCollection collection;
	private Stage dialogStage;
	private boolean okClicked;
	private String user;

	public NewHistoricDialogController() {

	}

	public void initialize() {
		
		searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				//System.out.println("Button: "+searchButton.getText());
				dialogStage.getScene().setCursor(Cursor.WAIT);
				//searchButton.setText("Searching...");	// FIXME la 1a vez cambia, pero la segunda no...
				Task<Void> task1 = new Task<Void>() {
					@Override
					public Void call() {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									handleSearch();
								} catch (ConnectivityException e) {
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
	public void handleSearch() throws ConnectivityException { // FIXME throws RateLimitException, DatabaseReadException

		int total = 0;

		if (!collection.getTweetStatus().isEmpty()) {
			collection.getTweetStatus().clear();
		}

		Query query = new Query();
		QueryResult queryResult = null;

		collection.setQuery(userQuery.getText());
		query.setQuery(collection.getQuery());

		System.out.println("Searching...");

		Timestamp ts_start = new Timestamp(System.currentTimeMillis());

		do {

			try {
				queryResult = twitter.search(query);
				// TODO twitter.addRateLimitStatusListener(e -> throw new
				// RateLimitException(););
			} catch (TwitterException e) {
				throw new ConnectivityException();
			}
			collection.saveTweetStatus(queryResult);
			total += queryResult.getCount();// mostrar en un pop up los tweets totales encontrados
			// downloadedTweets.setText("Downloaded tweets: "+total); // FIXME thread
			// también ¿?
			// queryResult.getRateLimitStatus(); -> muy interesante
		} while ((query = queryResult.nextQuery()) != null && total <= 200);

		Timestamp ts_end = new Timestamp(System.currentTimeMillis());

		System.out.println("Total: " + total);

		collection.addData(ts_start, ts_end, user);

		okClicked = true;

		System.out.println("Data saved...");
		dialogStage.close(); // FIXME !!!
//		searchButton.setText("Search");

	}

	@FXML
	private void handleCancel() {
		collection = null;
		dialogStage.close();
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void closeStage() {
		dialogStage.close();
	}
}
