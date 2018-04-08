package application.view;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FastLoginViewController {
	
	@FXML
	private TextField username; 

	private Stage currentStage;
	
	// Reference to the main application
	private Main main;

	/**
	 * The constructor, called before the initialize() method
	 */
	public FastLoginViewController() {

	}

	/**
	 * Initializes the controller class
	 * This method is automatically called after the fxml file has been loaded
	 */
	@FXML
	public void initialize() {

	}

	public void setStage(Stage stage) {
		currentStage = stage;
	}

	/**
	 * Is called by the main application to give a reference back to itself
	 * 
	 * @param main
	 */
	public void setMainApp(Main main) {
		this.main = main;
	}

	@FXML
	private void handleLogin() {
		String user = username.getText();
		main.manageFastLogin(user);
//		currentStage.close();
	}

}
