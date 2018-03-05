package tfg;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import tfg.model.Browser;
import tfg.model.TwitterUser;
import tfg.view.LoginViewController;
import tfg.view.SearchViewController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Main extends Application {
	
	Login login = new Login();
	TwitterUser u = new TwitterUser();
	private Stage primaryStage;
	
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
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void manageLogin() {
		login.setMainApp(this);
		login.createRequest();
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
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //#css!!
}
