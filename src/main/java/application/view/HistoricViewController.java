package application.view;

import java.util.List;

import application.Database;
import application.Main;
import application.model.HistoricSearch;
import application.model.TwitterSearch;
import application.utils.TweetUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import twitter4j.Status;

public class HistoricViewController extends AnchorPane {


	@FXML
	private ListView<String> historySearch;
	
	@FXML
	private ListView<String> currentSearch;
	
	private ObservableList<String> history = FXCollections.observableArrayList();

	private ObservableList<String> data = FXCollections.observableArrayList();
	

	private HistoricSearch search = new HistoricSearch();

//	private List<HistoricSearch> searchList = new ArrayList<HistoricSearch();

	private static SearchViewController searchController;
	
	private int from = 0;
	
	private int to = from + 100;
	
	public HistoricViewController() {

	}

	@FXML
	public void initialize() {
		
	}

	private void addSearch() {
		
		currentSearch.getItems().removeAll();
		
		int count = 1;

		from = Math.min(from, search.getTweetList().size());
		to = Math.min(to, search.getTweetList().size());
		
		for (Status tweet : search.getTweetList().subList(from, to)) {
			data.add(count+": @" + tweet.getUser().getScreenName() + " - " + tweet.getText());
			count++;
		}
		
		System.out.println("Original list count: "+search.getTweetList().size());
		
		//data.subList(0, 99);
		currentSearch.setItems(data);
		System.out.println("Showing 100 first tweets");
		history.add(search.getKeyword());
		historySearch.setItems(history);
	}

	@FXML
	private void handleNew() {

		boolean okClicked = searchController.newSearch(search);
		if (okClicked && search.getTweetList() != null) {
			addSearch();
		}
	}
	
	@FXML
	private void nextTweets() { // si se llega al final, mostrar pop up !!!!! URGENTIIIIISIMOOOO
		from = Math.min(from+100, search.getTweetList().size());
		to = Math.min(to, search.getTweetList().size());
		
		if(to == search.getTweetList().size()) {
			System.out.println("Has llegado al final de la lista");
			return;
		}

		int count = from+1;
		data.clear();
		for (Status tweet : search.getTweetList().subList(from, to)) {
			data.add(count+": @" + tweet.getUser().getScreenName() + " - " + tweet.getText().toString());
			count++;
		}
		//currentSearch.getItems().removeAll();
		currentSearch.setItems(data);
	}
	
	@FXML
	private void previousTweets() { // si se llega al inicio, mostrar pop up
		
		from = Math.max(from-100, 0);
		to = Math.min(to, search.getTweetList().size());
		
		if(from<0) {
			System.out.println("Has llegado al inicio de la lista");
			return;
		}
		
		int count = from+1;
		data.clear();
		for (Status tweet : search.getTweetList().subList(from, to)) {
			data.add(count+": @" + tweet.getUser().getScreenName() + " - " + tweet.getText());
			count++;
		}
		//currentSearch.getItems().removeAll();
		currentSearch.setItems(data);
	}
	
	/*
	 * añadir método para selección de la búsqueda correspondiente
	 */

	public static void init(SearchViewController controller) {
		searchController = controller;

	}

}
