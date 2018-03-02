package tfg;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import tfg.model.Browser;
import tfg.view.LoginViewController;
import tfg.view.SearchViewController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;

public class Main extends Application {
	
	Login login = new Login();
	private Stage primaryStage;
	
	// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //#css!!

	
	public Login getLogin() {
		return login;
	}
	
	//@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Twitter Searcher");
		
		showLogin();
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
	
/*
	public void startOAuth(Login oauth) throws Exception { // considerar quitar
		oauth.setMainApp(this);
		boolean success = oauth.getConnection();
		System.out.println(success);
		if(success) {
			showSearch();
		}
	}
*/	
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
//		System.out.println("New location: "+browser.getWebEngine().getLocation());
//		browser.getBaseCallbackURL();
		//Login login = new Login();
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
//			SearchViewController controller = loader.getController();
//			controller.setMainApp(this);
			
			
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
}
