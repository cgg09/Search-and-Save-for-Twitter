package tfg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import twitter4j.auth.AccessToken;

public class User {
	
	private StringProperty username;
	private StringProperty password;
	private AccessToken accessToken = null;
	
	public User(String username, String password) {
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
	}

	
	public String getUsername() {
		return username.get();
	}
	
	public void setUsername(String username) {
		this.username.set(username);
	}
	
	public String getPassword() {
		return password.get();
	}
	
	public void setPassword(String password) {
		this.password.set(password);
	}
	
	public void deletePassword() {
		this.password = null;
	}
	
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
}
