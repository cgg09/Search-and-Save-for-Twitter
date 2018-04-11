package application.view;

import java.sql.Timestamp;

import application.database.DBCollection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
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
	private void handleSearch() {
		
		int total = 0;
		
		if(!collection.getTweetList().isEmpty()) {
			collection.getTweetList().clear();
		}
		Timestamp ts_start = new Timestamp(System.currentTimeMillis());
		Query query = new Query();
		QueryResult queryResult;
		// poner elemento para indicar que busca!!!
		System.out.println("Searching...");
		
		try {
			collection.setQuery(userQuery.getText());			
			query.setQuery(collection.getQuery());		
			do {	
				queryResult = twitter.search(query);
				collection.addTweets(queryResult);
				total += queryResult.getCount(); // mostrar en un pop up los tweets totales encontrados
				//queryResult.getRateLimitStatus(); -> muy interesante
			} while((query = queryResult.nextQuery()) != null && total <= 200);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		System.out.println("Total: "+total);
		Timestamp ts_end = new Timestamp(System.currentTimeMillis());
	
		collection.addData(ts_start, ts_end, user);

		okClicked = true;
		
		System.out.println("Data saved...");
		
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
