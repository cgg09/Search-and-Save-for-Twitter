package application.view;

import java.sql.Timestamp;

import application.database.DBCollection;
import application.exceptions.DatabaseReadException;
import application.exceptions.RateLimitException;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class NewHistoricDialogController {

	Twitter twitter;

	@FXML
	private TextField userQuery;
	private DBCollection collection;
	private Stage dialogStage;
	private boolean okClicked;
	private String user;

	public NewHistoricDialogController() {

	}

	public void initialize() {

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
	public void handleSearch() { //FIXME throws RateLimitException, DatabaseReadException

		int total = 0;

		if (!collection.getTweetList().isEmpty()) {
			collection.getTweetList().clear();
		}
		
		Query query = new Query();
		QueryResult queryResult = null;

		collection.setQuery(userQuery.getText());
		query.setQuery(collection.getQuery());

		System.out.println("Searching...");
		ProgressIndicator pi = new ProgressIndicator();
		FlowPane root = new FlowPane();
		root.setPadding(new Insets(10));
        root.setHgap(10);
        root.getChildren().addAll(pi);
//        Scene scene = new Scene(root, 200, 150);
//        Stage progressStage = new Stage();
//        progressStage.setScene(scene);
//        progressStage.show();
        
		Timestamp ts_start = new Timestamp(System.currentTimeMillis());
		
		do {

			try {
				queryResult = twitter.search(query);
			} catch (TwitterException e) {
				e.printStackTrace(); //FIXME throw new RateLimitException();
			}
			collection.addTweets(queryResult);
			total += queryResult.getCount();// mostrar en un pop up los tweets totales encontrados
			// queryResult.getRateLimitStatus(); -> muy interesante
		} while ((query = queryResult.nextQuery()) != null && total <= 200);

		
		Timestamp ts_end = new Timestamp(System.currentTimeMillis());

		System.out.println("Total: " + total);
		
		collection.addData(ts_start, ts_end, user);

		okClicked = true;

		System.out.println("Data saved...");

//		progressStage.close();
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		collection = null;
		dialogStage.close();
	}

	public void setUser(String user) {
		this.user = user;
	}
}
