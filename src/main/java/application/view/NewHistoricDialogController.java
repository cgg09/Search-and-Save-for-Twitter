package application.view;

import java.sql.Timestamp;

import application.database.DB;
import application.database.DBCollection;
import application.model.HistoricSearch;
import application.model.TwitterSearch;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class NewHistoricDialogController {

	Twitter twitter;

	@FXML
	private TextField userQuery;
	private TwitterSearch search;
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

	public void setSearch(TwitterSearch search) {
		this.search = search;
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
		Timestamp ts_start = new Timestamp(System.currentTimeMillis());
		System.out.println("Searching...");
		int total = 0;
		try {
			search.setQuery(userQuery.getText());
			Query query = new Query();
			query.setQuery(search.getQuery());
			QueryResult queryResult;
			do {	
				queryResult = twitter.search(query);
				search.addTweets(queryResult);
				total = queryResult.getCount();
			} while((query = queryResult.nextQuery()) != null && total <= 500);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Timestamp ts_end = new Timestamp(System.currentTimeMillis());

		System.out.println("Collection exists? "+search.getCollection().toString());
		
		try {
			search.getCollection().addNewCollection(search, ts_start, ts_end,user);
		} catch(Exception e) {
			System.err.println("No se ha guardado bien");
		}
		
		
		for(Status tweet : search.getTweetList()) {
			search.getCollection().addTweet(tweet, search); //error aquí
		}
		
		okClicked = true;
		
		System.out.println("Data saved...");
		
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	public void setUser(String user) {
		this.user = user;
	}
}
