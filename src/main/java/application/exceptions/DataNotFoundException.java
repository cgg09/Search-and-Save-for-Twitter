package application.exceptions;

public class DataNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataNotFoundException(String message) {
		super(message);
	}

	/*
	 * public String toString() { return
	 * "This information was not found in the database."; }
	 */

}
