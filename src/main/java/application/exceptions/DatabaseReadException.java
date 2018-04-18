package application.exceptions;

public class DatabaseReadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseReadException(String message) {
		super(message);
	}

	/*
	 * public String toString() { return "There was an error reading the database.";
	 * }
	 */

}
