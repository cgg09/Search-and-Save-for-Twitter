package application.exceptions;

import java.sql.SQLException;

public class DatabaseReadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseReadException(String message, SQLException e) {
		super(message);
		e.printStackTrace();
	}

	/*
	 * public String toString() { return "There was an error reading the database.";
	 * }
	 */

}
