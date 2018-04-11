package application.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.auth.AccessToken;

public class TwitterUser {
	
	private final StringProperty username;
//	private AccessToken accessToken = null;
	
	public TwitterUser() {
		this(null);
		
	}
	
	public TwitterUser(String username) {
		this.username = new SimpleStringProperty(username);
	}

	
	public String getUsername() {
		return username.get();
	}
	
	public void setUsername(String username) {
		this.username.set(username);
	}
/*	
	public AccessToken getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
	
	public boolean isAuthorized() {
		if(accessToken != null)
			return true;
		return false;
	}
*/
}
