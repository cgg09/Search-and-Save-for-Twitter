package application.exceptions;

import twitter4j.TwitterException;

public class ConnectivityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectivityException(String message, TwitterException e) {
		super(message);//"You do not have internet connection. Please check it out before continue.");
		e.printStackTrace();
	}

	/*
	 * public String toString() { return
	 * "You are not connected to the internet. Check your connection."; }
	 */

}
