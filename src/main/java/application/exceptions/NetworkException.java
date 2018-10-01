package application.exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import twitter4j.TwitterException;

/**
 * Class to manage exceptions related to internet connections failed 
 * @author Maria Cristina, github: cgg09
 *
 */

public class NetworkException extends Exception {

	private static final long serialVersionUID = 1L;

	public NetworkException(String message, TwitterException e) {
		super(message);// TODO dejar o no dejar ?
		e.printStackTrace();
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("CONNECTIVITY FAILURE");
		alert.setHeaderText("Internet connection error");
		alert.setContentText(message);
		alert.showAndWait();	
	}
}

