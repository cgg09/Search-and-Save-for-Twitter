package application.view;

import java.util.Optional;

import application.Main;
import application.database.DBCollection;
import application.exceptions.DatabaseWriteException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SearchViewController extends AnchorPane {

	@FXML
	private MenuButton settingsButton;
	@FXML
	private Label username;
	@FXML
	private MenuItem logOut;
	@FXML
	private MenuItem deleteUser;
	
	private Stage currentStage;
	private Main main;

	public SearchViewController() {
		
	}

	@FXML
	public void initialize() {
				
		//initialize username menu
		username.setText(Main.getDBUserDAO().getUser());

		HistoricViewController.init(this);
	}

	public String getUsername() {
		return username.getText();
	}
	
	@FXML
	private void highlightUser() {
		username.setUnderline(true);
	}
	
	@FXML
	private void disguiseUser() {
		username.setUnderline(false);
	}
	
	public void newSearch(DBCollection c, HistoricViewController historicViewController) {

		//boolean okClicked = false;
		if (c.getType().equals("Historic")) {
			/*okClicked = */Main.showNewHistoricSearch(c,historicViewController);
		}
		//return okClicked;

	}
/*
	public void setMainApp(Main main) {
		this.main = main;
	}
*/
	public Main getMain() {
		return main;
	}
	
	@FXML
	private void signOut() {
		currentStage.close();
		Main.showLogin();
	}
	
	@FXML
	private void deleteUser() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("DELETE USER");
		alert.setHeaderText("Delete User");
		alert.setContentText("Are you sure you want to delete the user \"" + username.getText() + "\"?");

		ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOk) {
			try {
				Main.getDBUserDAO().deleteUser();
			} catch (DatabaseWriteException e) {
				e.printStackTrace();
			}

			username.setText("");

			currentStage.close();
			Main.showLogin();
		}
	}

	public void setStage(Stage primaryStage) {
		currentStage = primaryStage;
		
	}

}
