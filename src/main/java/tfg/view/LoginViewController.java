package tfg.view;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import tfg.Main;
import tfg.OAuthConnection;
import tfg.model.User;

public class LoginViewController {

	@FXML
	private TextField username;
	
	@FXML
	private PasswordField password;
	
	@FXML
	private Hyperlink link;
	
	protected HostServices host = null;
	
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
		
		link.setOnAction(this::handleLink);
		
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
	private void handleLogin() throws Exception {
		
		User user = new User(username.getText(), password.getText());
		OAuthConnection oauth = new OAuthConnection();
		main.startOAuth(user, oauth);
		
		//if(user.isAuthorized())
	}
	
	@FXML
	private void handleLink(ActionEvent event) {	
		System.out.println("You clicked"); // que pasa si clico 2 veces seguidas?
		link.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				host.showDocument("https://twitter.com/signup");
				System.out.println("Heeey well done! :)");
			
            }
		});
	}
	
	
}
