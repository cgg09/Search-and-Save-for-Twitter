package application.exceptions;

public class DatabaseWriteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseWriteException(String message) {
		super(message);
	}

	/*
	 * public String toString() { return
	 * "There was an error writing to the database."; }
	 */
}
