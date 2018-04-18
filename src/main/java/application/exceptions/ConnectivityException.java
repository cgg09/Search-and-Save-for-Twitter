package application.exceptions;

public class ConnectivityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectivityException(String message) {
		super(message);
	}

	/*
	 * public String toString() { return
	 * "You are not connected to the internet. Check your connection."; }
	 */

}
