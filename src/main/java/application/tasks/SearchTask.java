package application.tasks;

import application.database.DBCollection;
import application.exceptions.AccessException;
import application.exceptions.NetworkException;
import application.exceptions.RateLimitException;
import javafx.concurrent.Task;

public class SearchTask extends Task<Void> {
	
	DBCollection dbc;
	String query;
	
	public SearchTask(DBCollection collection, String query) {
		this.dbc = collection;
		this.query = query;
	}

	@Override
	protected Void call() throws Exception {
		try {
			dbc.manageSearch(query,this);
		} catch(AccessException | NetworkException | RateLimitException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void progressMessage(String m) {
		this.updateMessage(m);
	}

}
