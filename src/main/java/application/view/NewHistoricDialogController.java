package application.view;

import application.model.HistoricSearch;
import application.model.TwitterSearch;
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
	private TextField keyword;
	private TwitterSearch search;
	private Stage dialogStage;
//	private HistoricSearch search = new HistoricSearch();
	
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
	
	@FXML
	private void handleSearch() {
		search.setKeyword(keyword.getText());
		Query query = new Query();
		query.setQuery(keyword.getText());
		try {
			QueryResult queryResult = twitter.search(query);
			search.addTweets(queryResult.getTweets());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//llamar al servicio que realiza las búsquedas!!!!
		dialogStage.close();
	}
	
	@FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
