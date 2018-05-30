package application.exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import twitter4j.TwitterException;

public class RateLimitException extends Exception {

	/**
	 * 
	 */
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

	/*
	 * public String toString() { return
	 * "You have exceeded the limit of tweet downloads. Please wait for 15 minutes to continue."
	 * ; }
	 */

}
