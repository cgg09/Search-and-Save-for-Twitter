package application.exceptions;

public class AccessException extends Exception {
	
	public AccessException() {
		super("You do not have access to your Twitter account. Check out your credentials.");
	}

}
