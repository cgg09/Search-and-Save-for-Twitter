package application;

import java.io.File;
import java.io.IOException;

import application.database.DBCollection;
import application.database.DBUserDAO;
import application.database.DatabaseDAO;
import application.exceptions.AccessException;
import application.exceptions.NetworkException;
//import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.utils.Browser;
import application.utils.TwitterSessionDAO;
import application.view.HistoricViewController;
import application.view.LoginViewController;
import application.view.NewHistoricDialogController;
import application.view.ProgressController;
import application.view.SearchViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Main extends Application {

	private static Main instance;
	private static DatabaseDAO databaseDAO;
	private static DBUserDAO dbUserDAO;
	private static TwitterSessionDAO twitterSessionDAO;
	private static Stage primaryStage;

	private static Login login;

	public static Main getInstance() {
		return instance;
	}
	
	public static Login getLogin() {
		return login;
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

	public static TwitterSessionDAO getTwitterSessionDAO() {
		return twitterSessionDAO;
	}

	public static void setTwitterSessionDAO(TwitterSessionDAO twitterSessionDAO) {
		Main.twitterSessionDAO = twitterSessionDAO;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public void start(Stage primaryStage) {
		instance = this;
		Main.getInstance();
		Main.primaryStage = primaryStage;
		Main.primaryStage.setTitle("Twitter Searcher");

		showLogin();
	}

	/**
	 * Initializes the login view
	 */
	public static void showLogin() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/LoginView.fxml"));
		AnchorPane loginView = null;
		try {
			loginView = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene scene = new Scene(loginView);
		primaryStage.setScene(scene);
		primaryStage.show();

		LoginViewController controller = loader.getController();
		controller.setStage(primaryStage);
		login = new Login();
	}

	public static boolean manageNewLogin() throws NetworkException, AccessException {
		boolean d = false;
		setDBUserDAO(DBUserDAO.getInstance());
		setTwitterSessionDAO(TwitterSessionDAO.getInstance());
		twitterSessionDAO.setTwitterInstance();
		d = login.createRequest(twitterSessionDAO.getTwitter(), dbUserDAO);
		return d;
	}

	public static boolean manageFastLogin(String user) throws NetworkException {
		setDBUserDAO(DBUserDAO.getInstance());
		boolean done = false;
		setTwitterSessionDAO(TwitterSessionDAO.getInstance());
		twitterSessionDAO.setTwitterInstance();
		System.out.println("Twitter instance setted");
		try {
			done = login.retrieveSession(twitterSessionDAO.getTwitter(), user, dbUserDAO);
		} catch (AccessException e) {
			e.printStackTrace();
		}
		System.out.println("Session retrieved");
		return done;
	}

	/**
	 * Initializes the webView for the first login
	 * @return 
	 */
	public static void showWebView(String URL) {

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
	 * 
	 * @return
	 */
	public static boolean showSearch() {
		// Load login from fxml file
		System.out.println("Loading window ..?");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/SearchView.fxml"));
		AnchorPane searchView = new AnchorPane();
		try {
			searchView = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Show the scene containing the search view
		Scene scene = new Scene(searchView);
		primaryStage.setScene(scene);
		primaryStage.show();
		// Give the controller access to the main app
		SearchViewController controller = loader.getController();
		controller.setStage(primaryStage);
		return true;

	}

	public static void showNewHistoricSearch(DBCollection c, HistoricViewController historicViewController) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/NewHistoricDialog.fxml"));
		AnchorPane page = null;
		try {
			page = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage dialogStage = new Stage();
		dialogStage.setTitle("New historic search");
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		NewHistoricDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setTwitter(twitterSessionDAO.getTwitter());
		controller.setCollection(c);
		controller.setHistoricView(historicViewController);
		dialogStage.showAndWait();
	}

	public static ProgressController showProgressBar(String title) {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/ProgressLayout.fxml"));
		AnchorPane page = null;
		try {
			page = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage progressStage = new Stage();
		progressStage.setTitle("Progress");
		progressStage.initOwner(primaryStage);
		Scene scene = new Scene(page);
		progressStage.setScene(scene);
		progressStage.show();

		ProgressController controller = loader.getController();
		controller.setStage(progressStage);
		controller.setProcessTitle(title);
		return controller;
	}

	public static void main(String[] args) {

		String path = "src/main/resources/twitter.db";
		// Eclipse path: "src/main/resources/twitter.db";
		// Ant build path: "resources/twitter.db";
		System.out.println("Starting...");
		File file = new File(path);
		getInstance();
		Main.setDatabaseDAO(DatabaseDAO.getInstance(path));

		if (!file.exists()) {
			System.out.println("Database does not exist. Create a new one");
			databaseDAO.connect();
			try {
				databaseDAO.createUserTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
			try {
				databaseDAO.createCollectionTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
			try {
				databaseDAO.createTweetTable();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				databaseDAO.checkDatabase();
			} catch (DatabaseReadException e) {
				e.printStackTrace();
			}
		}

		launch(args);
	}
	// css -->
	// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
}
