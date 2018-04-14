package application.view;

import java.util.List;

import application.Main;
import application.database.DBUserDAO;
import application.exceptions.DatabaseReadException;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class LoginViewController {
	
	private Stage currentStage;
	
	// Reference to the main application
	private Main main;
	
	@FXML
	private MenuButton loginButton;
	
	/**
	 * The constructor, called before the initialize() method
	 */
	public LoginViewController() {		
		
	}

	/**
	 * Initializes the controller class
	 * This method is automatically called after the fxml file has been loaded
	 * @throws DatabaseReadException 
	 */
	@FXML
	public void initialize() throws DatabaseReadException {
		
		Main.setDBUserDAO(DBUserDAO.getInstance());
		List<String> users = Main.getDBUserDAO().getUsers();
		if(users!=null) {
			for(String u : users) {
				MenuItem m = new MenuItem(u);
				loginButton.getItems().add(m);
			}
		} else {
			//TODO mostrar botón "opacado", o difuminado ... :|
		}
		
		//FIXME loginButton.parentProperty().addListener().getChildrenUnmodifiable(); ¿?
		
		loginButton.setOnAction(event -> {
		    System.out.println("Option 3 selected via Lambda");
		});
		
		
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
	private void handleLogIn() {
		main.showFastLogin();
	}
/*	
	@FXML
	private void handleSignIn() {
		main.manageFastLogin();
		currentStage.close();
	}*/
	
	
	/**
	 * When the user clicks the login button
	 * @throws Exception
	 */
	@FXML
	private void handleSignUp() throws Exception {
		main.manageNewLogin();
		currentStage.close();
	}	
	
}
