package tfg;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import tfg.model.Browser;
import tfg.view.LoginViewController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class Main extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //#css!!

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
	
	public void startOAuth(OAuthConnection oauth) throws Exception {
		oauth.setMainApp(this);
		boolean success = oauth.getConnection();
		if(success) {
			showSearch();
		}
	}
	
	/**
	 * Initializes the webView inside the root layout
	 */
	public void showWebView(String URL) {
		
		// create the scene
		Stage stage = new Stage();
		stage.setTitle("Web View");
		Scene scene = new Scene(new Browser(URL), 750, 500, Color.web("#666970"));
		stage.setScene(scene);
		// scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
		stage.show();	
		
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
			rootLayout.setCenter(searchView);
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
