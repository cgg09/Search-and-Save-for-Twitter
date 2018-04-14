package application.exceptions;

public class RateLimitException extends Exception {

	public RateLimitException() {
		super("You exceeded the limit of tweet downloads. Please wait for 15 minutes to continue your search.");
	}

}
