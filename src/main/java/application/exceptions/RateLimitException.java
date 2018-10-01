package application.exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import twitter4j.TwitterException;

/**
 * Class to manage exceptions related to exceeding rate limitings of Twitter API 
 * @author Maria Cristina, github: cgg09
 *
 */

public class RateLimitException extends Exception {

	private static final long serialVersionUID = 1L;

	public RateLimitException(String message,TwitterException e) {
		super(message);

		Integer time = e.getRateLimitStatus().getSecondsUntilReset();

		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("RATE LIMIT FAILURE");
		alert.setHeaderText("Rate Limit searching tweets");
		alert.setContentText(message+".You have to wait "+time.toString()+" seconds before continue searching.");
		alert.showAndWait();
	}

}
