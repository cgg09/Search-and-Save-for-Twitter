package application.exceptions;

import java.sql.SQLException;

/**
 * Class to manage exceptions related to writing errors of the database 
 * @author Maria Cristina, github: cgg09
 *
 */

public class DatabaseWriteException extends Exception {

	private static final long serialVersionUID = 1L;

	public DatabaseWriteException(String message, SQLException e) {
		super(message);
		e.printStackTrace();
	}

}
