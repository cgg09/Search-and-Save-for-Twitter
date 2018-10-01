package application.exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import twitter4j.TwitterException;

/**
 * Class to manage exceptions related to an frustrated connection between the user and Twitter
 * @author Maria Cristina, github: cgg09
 *
 */

public class AccessException extends Exception {

	private static final long serialVersionUID = 1L;

	public AccessException(String message, TwitterException e) {
		
		super(message);
		e.printStackTrace();
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("ACCESS FAILURE");
		alert.setHeaderText("Access error");
		alert.setContentText(message+" Please check out your Twitter settings account.");
		alert.showAndWait();
	}

}
