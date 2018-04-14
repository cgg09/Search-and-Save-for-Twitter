package application.exceptions;

public class DatabaseReadException extends Exception{
	
	public DatabaseReadException() {
		super("There was an error reading the database.");
	}

}
