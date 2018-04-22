package application.exceptions;

public class ConnectivityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectivityException() {
		super("You do not have internet connection. Please check it out before continue.");
	}

	/*
	 * public String toString() { return
	 * "You are not connected to the internet. Check your connection."; }
	 */

}
