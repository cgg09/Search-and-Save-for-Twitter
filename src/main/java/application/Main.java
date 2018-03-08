package application;
	
import java.io.IOException;

import application.model.Browser;
import application.model.HistoricSearch;
import application.model.LiveSearch;
import application.model.TwitterSearch;
import application.model.TwitterUser;
import application.view.LoginViewController;
import application.view.NewHistoricDialogController;
import application.view.SearchViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Main extends Application {
	
	Login login = new Login();
	TwitterUser u = new TwitterUser();
	Twitter twitter = TwitterFactory.getSingleton();
	
	private ObservableList<HistoricSearch> historicSearch = FXCollections.observableArrayList();
	private ObservableList<LiveSearch> liveSearch = FXCollections.observableArrayList();
	
	private Stage primaryStage;
	
	public Twitter getTwitterInstance() {
		return twitter;
	}
	
	public TwitterUser getUser() {
		return u;
	}
	
	//@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Twitter Searcher");
		
		showLogin();
//		Database.connect(); --> de momento no va, seguir con ello
	}
	
	/**
	 * Initializes the login view
	 */
	public void showLogin() {
		try {
			// Load login from fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/LoginView.fxml"));
			AnchorPane loginView = (AnchorPane) loader.load();
			
			// Show the scene containing the login view
			Scene scene = new Scene(loginView);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// Give the controller access to the main app
			LoginViewController controller = loader.getController();
			controller.setMainApp(this);
			controller.setStage(primaryStage);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void manageLogin() {
		login.setMainApp(this);
		login.createRequest(twitter);
	}
	
	/**
	 * Initializes the webView inside the root layout
	 */
	public void showWebView(String URL) {
		
		// create the scene
		Browser browser = new Browser(URL);
		Stage stage = new Stage();
		stage.setTitle("Web View");
		Scene scene = new Scene(browser, 750, 500, Color.web("#666970"));
		stage.setScene(scene);
		stage.show();
		browser.setStage(stage);
		login.retrieveTokens(browser);
	}
	
	/**
	 * Initializes the search view inside the root layout
	 */
	public void showSearch() {
		try {
			// Load login from fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/SearchView.fxml"));
			AnchorPane searchView = (AnchorPane) loader.load();
			
			// Show the scene containing the search view
			Scene scene = new Scene(searchView);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// Give the controller access to the main app
			SearchViewController controller = loader.getController();
			controller.setMainApp(this);
			controller.setUsername(getUser().getUsername());
			
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showNewHistoricSearch(TwitterSearch search) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/NewHistoricDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Nueva búsqueda modo histórico");
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			NewHistoricDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setTwitter(twitter);
			controller.setSearch(search);
			
			dialogStage.showAndWait();
		
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //#css!!
}
