package application;
	
import java.io.File;
import java.io.IOException;

import application.database.DBCollection;
import application.database.DBUserDAO;
import application.database.DatabaseDAO;
import application.exceptions.DatabaseReadException;
import application.exceptions.RateLimitException;
import application.utils.Browser;
import application.utils.TwitterUser;
import application.view.FastLoginViewController;
import application.view.LoginViewController;
import application.view.NewHistoricDialogController;
import application.view.SearchViewController;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Main extends Application {
	
	private static DatabaseDAO databaseDAO;
	private static DBUserDAO dbUserDAO;
	
	
	Login login = new Login();
	TwitterUser u = new TwitterUser();
	Twitter twitter = TwitterFactory.getSingleton();	
	
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
	
	public void manageNewLogin() {
		login.setMainApp(this);
		setDBUserDAO(DBUserDAO.getInstance());
		login.createRequest(twitter,dbUserDAO);		
	}
	
	/**
	 * Login view for a fast login
	 */
	public void showFastLogin() {
		try {
			// Load login from fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/FastLoginView.fxml"));
			AnchorPane fastLoginView = (AnchorPane) loader.load();
			
			// Show the scene containing the login view
			Scene scene = new Scene(fastLoginView);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// Give the controller access to the main app
			FastLoginViewController controller = loader.getController();
			controller.setMainApp(this);
			controller.setStage(primaryStage);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void manageFastLogin(String user) { //FIXME throws DatabaseReadException
		login.setMainApp(this);
		setDBUserDAO(DBUserDAO.getInstance());
		boolean check = dbUserDAO.checkUser(user);
		if(check) {
			login.retrieveSession(twitter,user,dbUserDAO);
		}
		else {
			System.out.println("Lo siento, pero este usuario no está registrado en esta aplicación. Intenta de nuevo.");
		}
	}
		
	/**
	 * Initializes the webView for the first login
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
	
	public boolean showNewHistoricSearch(DBCollection c) {
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
			controller.setUser(u.getUsername());
			controller.setCollection(c);
			
			dialogStage.showAndWait();
			
			
/*			RotateTransition rotate = new RotateTransition();
			rotate.setOnFinished(e -> {
				try {
					controller.handleSearch();
				} catch (RateLimitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DatabaseReadException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			});
			rotate.play();
*/			
			return controller.isOkClicked();
		
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static DatabaseDAO getDatabaseDAO() {
		return databaseDAO;
	}
	
	public static void setDatabaseDAO(DatabaseDAO databaseDAO) {
		Main.databaseDAO = databaseDAO;
	}
	
	public static DBUserDAO getDBUserDAO() {
		return dbUserDAO;
	}
	
	public static void setDBUserDAO(DBUserDAO dbUserDAO) {
		Main.dbUserDAO = dbUserDAO;
	}
	
	
	public static void main(String[] args) {
		
		String path = "src/main/resources/twitter.db";	
		File file = new File(path);
		setDatabaseDAO(DatabaseDAO.getInstance(path));
		
		if(!file.exists()) {
			System.out.println("Database does not exist. Create a new one");
			databaseDAO.createDatabase();
		} else {
			databaseDAO.connect();
		}
		
		launch(args);
	}
	
	// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //#css!!
}
