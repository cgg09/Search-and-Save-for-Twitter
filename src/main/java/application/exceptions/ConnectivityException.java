package application.exceptions;

public class ConnectivityException extends Exception {
	
	public ConnectivityException() {
		super("You are not connected to the internet. Check your connection.");
	}

}
