package application.tasks;

import application.Main;
import application.exceptions.NetworkException;
import javafx.concurrent.Task;

/**
 * Class to manage sign up connections
 * @author Maria Cristina, github: cgg09
 *
 */

public class SignUpTask extends Task<Void> {
	
	public SignUpTask() {
		
	}

	@Override
	protected Void call() throws Exception {	
		try {
			Main.manageNewLogin(this);
		} catch (NetworkException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public void progressMessage(String m) {
		this.updateMessage(m);
	}

}
