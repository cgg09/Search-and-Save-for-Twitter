package application.exceptions;

public class AccessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccessException(String message) {
		super(message);
	}

	/*
	 * public String toString() { return
	 * "You do not have access to your Twitter account. Please, check out your credentials."
	 * ; }
	 */
}
