package application.tasks;

import application.Main;
import application.exceptions.NetworkException;
import javafx.concurrent.Task;

public class LoginTask extends Task<Void> {
	
	private String u;
	
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

}
