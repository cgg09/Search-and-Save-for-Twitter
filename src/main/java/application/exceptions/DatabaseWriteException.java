package application.exceptions;

public class DatabaseWriteException extends Exception {

	public DatabaseWriteException() {
		super("There was an error writing to the database.");
	}

}
