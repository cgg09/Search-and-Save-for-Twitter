package application.view;

import java.util.List;
import java.util.Vector;

import application.Main;
import application.database.DBUserDAO;
import application.exceptions.AccessException;
import application.exceptions.ConnectivityException;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class LoginViewController {

	@FXML
	private MenuButton loginButton;
	private Stage currentStage;
	private Main main;
	
	
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
	public void initialize() { //FIXME throws DatabaseReadException
		
		Main.setDBUserDAO(DBUserDAO.getInstance());
		List<String> users = new Vector<String>();
		try {
			users = Main.getDBUserDAO().getUsers();
		} catch (DatabaseReadException | DataNotFoundException e2) {
			e2.printStackTrace();
		}
		if(users!=null) {
			for(String u : users) {
				MenuItem m = new MenuItem(u);
				m.setOnAction(e -> {
					MenuItem source = (MenuItem) e.getSource(); 
					try {
						main.manageFastLogin(source.getText());
					} catch (ConnectivityException e1) {
						e1.printStackTrace();
					}
				});
				loginButton.getItems().add(m);
			}
		} else {
			//TODO mostrar botón "opacado", o difuminado ... :|
		}
		
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
	 * @throws ConnectivityException 
	 * @throws AccessException 
	 * @throws Exception
	 */
	@FXML
	private void handleSignUp() throws ConnectivityException, AccessException {
		main.manageNewLogin();
		currentStage.close();
	}	
	
}
