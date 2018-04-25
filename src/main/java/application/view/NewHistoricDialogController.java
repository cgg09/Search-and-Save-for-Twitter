package application.view;

import java.sql.Timestamp;

import application.database.DBCollection;
import application.exceptions.ConnectivityException;
import javafx.fxml.FXML;
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
	private Button searchButton;
	private DBCollection collection;
	private Stage dialogStage;
	private boolean okClicked;
	private String user;
	

	public NewHistoricDialogController() {

	}

	public void initialize() {

		/*searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Task<?> task = new Task<Object>() {
					@Override
					protected Integer call() throws Exception {
						int iterations;
						dialogStage.getScene().setCursor(Cursor.WAIT); // Change cursor to wait style
						for (iterations = 0; iterations < 100000; iterations++) {
							System.out.println("Iteration " + iterations);
						}
						handleSearch();
						dialogStage.getScene().setCursor(Cursor.DEFAULT); // Change cursor to default style
						return null;
					}
				};
				Thread th = new Thread(task);
				th.setDaemon(true);
				th.start();
			}
		});*/

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

		searchButton.setText("Searching..."); // FIXME thread también ¿?
		
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
			downloadedTweets.setText("Downloaded tweets: "+total); // FIXME thread también ¿?
			// queryResult.getRateLimitStatus(); -> muy interesante
		} while ((query = queryResult.nextQuery()) != null && total <= 200);

		Timestamp ts_end = new Timestamp(System.currentTimeMillis());

		System.out.println("Total: " + total);

		collection.addData(ts_start, ts_end, user);

		okClicked = true;

		System.out.println("Data saved...");
		dialogStage.close(); // FIXME !!!
		searchButton.setText("Search");

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
