package tfg.view;

import javafx.fxml.FXML;

import tfg.Login;
import tfg.Main;

public class LoginViewController {


//	@FXML
//	private Hyperlink link;
	
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
		//Login login = main.getLogin();
		//login.createRequest();
	}	
	
}
