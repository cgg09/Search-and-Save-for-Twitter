package tfg;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tfg.model.User;
import tfg.view.LoginViewController;
import tfg.view.WebViewController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Main extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); #css!!

	//@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Twitter Searcher");
		
		initRootLayout();
		
		showLogin();
//		showSearch();
	}
	
	/**
	 * Initializes the root layout
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			// Show the scene containing the root layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the login inside the root layout
	 */
	public void showLogin() {
		try {
			// Load login from fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/LoginView.fxml"));
			AnchorPane loginView = (AnchorPane) loader.load();
			
			// Show the scene containing the login view
			rootLayout.setCenter(loginView);
			
			// Give the controller access to the main app
			LoginViewController controller = loader.getController();
//			controller.setPrimaryStage(primaryStage);
			controller.setMainApp(this);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startOAuth(User user, OAuthConnection oauth) throws Exception {
		oauth.setMainApp(this);
		boolean success = oauth.getConnection(user);
		if(success) {
			showSearch();
		}
	}
	
	/**
	 * Initializes the webView inside the root layout
	 */
	public void showWebView(String URL) {
		try {
			//FXMLLoader loader = new FXMLLoader();
			//loader.setLocation(Main.class.getResource("view/WebView.fxml"));
			//AnchorPane webView = (AnchorPane) FXMLLoader.load(Main.class.getResource("view/WebView.fxml"));
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/WebView.fxml"));
			loader.setControllerFactory(clazz -> {
			    if (clazz == WebViewController.class) {
			        return new WebViewController(URL);
			    } else {
			        // default behavior:
			        try {
			            return clazz.newInstance();
			        } catch (Exception exc) {
			            throw new RuntimeException(exc);
			        }
			    }
			});
			
			AnchorPane webView = (AnchorPane) loader.load();
			
			
			
			// Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Twitter OAuth");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(webView);
	        dialogStage.setScene(scene);

	        // Set the person into the controller.
//	        WebViewController controller = loader.getController();
//			controller.setMainApp(this);
//	        controller.setDialogStage(dialogStage);
	       

	        // Show the dialog and wait until the user closes it
//	        dialogStage.showAndWait();
			
			
			
			
			
			/*
			WebView browser = new WebView();
	    	WebEngine webEngine = browser.getEngine();
	    	webEngine.load(URL);
			*/
			
			
			//Show the scene containing the web view
//			rootLayout.setCenter(new WebViewController(URL));
		} catch(IOException e) {
			e.printStackTrace();
		}
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
