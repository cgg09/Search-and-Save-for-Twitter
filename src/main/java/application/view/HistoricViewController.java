package application.view;

import java.util.List;

import application.Main;
import application.model.HistoricSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.AnchorPane;
import twitter4j.Status;

public class HistoricViewController extends AnchorPane {

	@FXML
	private ListView historySearch;
	
	@FXML
	private ListView currentSearch;
	
	private static Main main;
	
	HistoricSearch search = new HistoricSearch();
	
	List<HistoricSearch> searchList = null;
	
	private static SearchViewController searchController;
	
	public HistoricViewController() {
		
	}
	
	@FXML
	public void initialize() {
		//setMainApp();
	}
	
	private void addSearch(List<Status> tweets) {
		ObservableList<Status> data = FXCollections.observableArrayList(tweets);
		currentSearch.setItems(data);
//		searchList.add(search);
//		historySearch.setItems();
	}
	
	@FXML
	private void handleNew() {
		
		searchController.newSearch(search);
		if(search.getTweetList()!=null) {
			addSearch(search.getTweetList());
		}
	}

	public static void init(SearchViewController controller) {
		searchController = controller;
		
	}
	
}
