package application.exceptions;

public class RateLimitException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RateLimitException(String message) {
		super(message);
	}

	/*
	 * public String toString() { return
	 * "You have exceeded the limit of tweet downloads. Please wait for 15 minutes to continue."
	 * ; }
	 */

}
