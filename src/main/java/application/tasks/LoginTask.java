package application.tasks;

import application.Main;
import application.exceptions.AccessException;
import application.exceptions.NetworkException;
import javafx.concurrent.Task;

/**
 * Class to manage sign in connections in background 
 * @author Maria Cristina, github: cgg09
 *
 */

public class LoginTask extends Task<Void> {
	
	private String u;
	private Exception e; // FIXME pending to check access exceptions
	
	public LoginTask(String user) {
		this.u = user;
	}

	@Override
	protected Void call() throws Exception {
		try {
			Main.manageFastLogin(u,this);
		} catch (NetworkException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public void progressMessage(String m) {
		this.updateMessage(m);
	}
	
	public void setException(Exception e) {
		this.e = e;
	}
	
	public Throwable getExceptionData() {
		return this.getException();
	}

}
