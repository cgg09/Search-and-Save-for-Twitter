package application.exceptions;

import java.sql.SQLException;

public class DatabaseWriteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseWriteException(String message, SQLException e) {
		super(message);
		e.printStackTrace();
	}

	/*
	 * public String toString() { return
	 * "There was an error writing to the database."; }
	 */
}
