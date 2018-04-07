package application.view;

import java.sql.Date;
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
	private TableView<Integer> historySearch;	// En el futuro será tipo DBCollection !!!
	
	@FXML
	private TableColumn<Integer, Date> dateColumn;
	
	@FXML
	private TableColumn<Integer, String> keywordColumn;
	
	@FXML
	private ListView<String> currentSearch;
	
	private ObservableList<String> history = FXCollections.observableArrayList();

	private ObservableList<String> data = FXCollections.observableArrayList();
	

	private HistoricSearch search = new HistoricSearch();

	private static SearchViewController searchController;
	
	private int from = 0;
	
	private int to;
	
	public HistoricViewController() {

	}

	@FXML
	public void initialize() {
		
		dateColumn.setCellFactory(
				cellData -> cellData.getValue().dateColumnProperty());
		
		keywordColumn.setCellValueFactory(
				cellData -> cellData.getValue().keywordColumnProperty());
		
		//addSearch(null);
		
		historySearch.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {System.out.println("oldValue: "+oldValue+", newValue: "+newValue); 
													System.out.println("Keyword: "+search.getKeyword());
													addSearch(newValue);});
	}
	
	private void addCollection() {
		history.add(search.getKeyword());
		historySearch.setItems(history);
	}

	private void addSearch(String keyword) {
		
		System.out.println("Data empty?: "+data.isEmpty());
		
		if(!data.isEmpty()) {
			data.clear();
			System.out.print("Data cleaned\n");
		}
		
		int count = 1; // esto se va cuando esté hecha la tableview

		if(keyword == search.getKeyword()) { // recuperar la collection_id correcta en base a la keyword y a la fecha
			
			from = Math.min(from, search.getTweetList().size());
			to = Math.min(from+50, search.getTweetList().size());
			
			System.out.println(from+" "+to);
			
			for (Status tweet : search.getTweetList().subList(from, to)) {
				data.add(count+": @" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				count++;	// esto se va cuando esté hecha la tableview
			}
			
			System.out.println("Original list count: "+search.getTweetList().size());
			
			currentSearch.setItems(data);			
		}
		else {
			System.out.println("Esto sigue sin funcionar :(");
		}
		
	}

	@FXML
	private void handleNew() {

		boolean okClicked = searchController.newSearch(search);
		if (okClicked && search.getTweetList() != null) {
			System.out.println("THIS: "+this.getClass().getName());
			addCollection();
			addSearch(search.getKeyword());
		}
	}
	
	@FXML
	private void nextTweets() {
		
		if(to == search.getTweetList().size()) { // FIN DE LISTA: ensombrecer el botón para impedir el click !!
			System.out.println("Has llegado al final de la lista");
			return;
		}
		from = Math.min(from+50, search.getTweetList().size());
		to = Math.min(from+50, search.getTweetList().size());
		
		int count = from+1;	// esto se va cuando esté hecha la tableview
		data.clear();
		for (Status tweet : search.getTweetList().subList(from, to)) {
			data.add(count+": @" + tweet.getUser().getScreenName() + " - " + tweet.getText().toString());
			count++;	// esto se va cuando esté hecha la tableview
		}
		
		currentSearch.setItems(data);
	}
	
	@FXML
	private void previousTweets() {
		
		if(from == 0) { // INICIO DE LISTA: ensombrecer el botón para impedir el click !!
			System.out.println("Has llegado al inicio de la lista");
			return;
		}
		
		to = Math.min(from, search.getTweetList().size());
		from = Math.max(to-50, 0);
		
		int count = from+1;	// esto se va cuando esté hecha la tableview
		data.clear();
		for (Status tweet : search.getTweetList().subList(from, to)) {
			data.add(count+": @" + tweet.getUser().getScreenName() + " - " + tweet.getText());
			count++;	// esto se va cuando esté hecha la tableview
		}

		currentSearch.setItems(data);
	}
	
	/*
	 * añadir método para seleccionar una búsqueda y mostrarla en la vista de tweets
	 */

	
	
	@FXML
	private void handleExport() {
		Database.exportCSV(search.getKeyword());
	}
	
	public static void init(SearchViewController controller) {
		searchController = controller;

	}

}
