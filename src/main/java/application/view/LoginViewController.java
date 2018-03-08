package application.view;

import application.Main;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class LoginViewController {


//	@FXML
//	private Hyperlink link;
	
	private Stage currentStage;
	
	// Reference to the main application
	private Main main;
	
	/**
	 * The constructor, called before the initialize() method
	 */
	public LoginViewController() {
		
	}

	/**
	 * Initializes the controller class
	 * This method is automatically called after the fxml file has been loaded
	 */
	@FXML
	public void initialize() {
		
//		link.setOnAction(this::handleLink);
		
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
	
	/**
	 * When the user clicks the login button
	 * @throws Exception
	 */
	@FXML
	private void handleLogin() throws Exception {
		main.manageLogin();
		currentStage.close();
	}	
	
}
