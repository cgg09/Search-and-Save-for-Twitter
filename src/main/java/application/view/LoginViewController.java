package application.view;

import java.util.List;

import application.Main;
import application.database.DBUserDAO;
import application.exceptions.DatabaseReadException;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class LoginViewController {
	
	private Stage currentStage;
	
	// Reference to the main application
	private Main main;
	
	@FXML
	private MenuButton loginButton;
//	private ObservableList<MenuItem> options;
	
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
		List<String> users = Main.getDBUserDAO().getUsers();
		if(users!=null) {
			for(String u : users) {
				MenuItem m = new MenuItem(u);
				m.setOnAction(e-> {
					MenuItem source = (MenuItem) e.getSource(); 
					main.manageFastLogin(source.getText());	// FIXME currentStage.close(); ??
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
	 * @throws Exception
	 */
	@FXML
	private void handleSignUp() {
		main.manageNewLogin();
		currentStage.close();
	}	
	
}
