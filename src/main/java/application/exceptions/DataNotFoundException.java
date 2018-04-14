package application.exceptions;

public class DataNotFoundException extends Exception {

	public DataNotFoundException() {
		super("This information is not in the database.");
	}

}
