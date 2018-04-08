package application.view;

import application.Main;
import application.model.HistoricSearch;
import application.model.LiveSearch;
import application.model.TwitterSearch;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class SearchViewController extends AnchorPane {
	
	@FXML
	private Label username;
	private Main main;
	
	public SearchViewController() {
		
	}
	
	@FXML
	public void initialize() {
		HistoricViewController.init(this);
		//LiveViewController.init(this);
	}
	
	public String getUsername() {
		return username.getText();
	}
	
	public void setUsername(String u) {
		username.setText(u);
	}
	
	public boolean newSearch(TwitterSearch search) {
		
		boolean okClicked = false;
		
		if(search instanceof HistoricSearch){
			okClicked = main.showNewHistoricSearch(search);
			
		}
		else if(search instanceof LiveSearch) {
			//okClicked = main.showNewLiveSearch(search);
		}
		return okClicked;
		
	}
	
	public void setMainApp(Main main) {
		this.main = main;
	}
	
	public Main getMain() {
		return main;
	}
	

}
