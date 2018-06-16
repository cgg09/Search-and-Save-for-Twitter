package application;

import java.util.HashMap;
import java.util.Map;

import application.database.DBUserDAO;
import application.exceptions.AccessException;
import application.exceptions.NetworkException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.utils.Browser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Login {

	private Twitter twitter;
	private AccessToken accessToken = null;
	private RequestToken requestToken = null;
	private WebEngine webEngine;
	private DBUserDAO dbu;
	private int UNAUTHORIZED = 401;

	public Login() {

	}
	
	public RequestToken getRequestToken() {
		return requestToken;
	}

	/**
	 * Request an authorization to Twitter
	 * @return 
	 * 
	 * @throws ConnectivityException
	 * @throws AccessException
	 */
	public boolean createRequest(Twitter twitter, DBUserDAO dbu) throws NetworkException, AccessException {
		
		this.dbu = dbu;
		this.twitter = twitter;

		try {
			requestToken = twitter.getOAuthRequestToken(Main.getTwitterSessionDAO().getCallbackUrl());
		} catch (TwitterException e) {
			if (e.getStatusCode() == UNAUTHORIZED) {
				throw new AccessException("401: Unable to get the access token. Please check your credentials.", e);
			} else {
				throw new NetworkException("You do not have internet connection. Please check it out before continue",
						e);
			}
		}
		
		return true;

	}

	/**
	 * New login process 2n step: Retrieve tokens from callback url
	 * @return 
	 */
	public void retrieveTokens(Browser browser) {
		
		webEngine = browser.getWebEngine();
		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

			@Override
			public void changed(ObservableValue<? extends javafx.concurrent.Worker.State> observable,
					javafx.concurrent.Worker.State oldState, javafx.concurrent.Worker.State newState) {

				if (newState.equals(javafx.concurrent.Worker.State.FAILED)) {
					String location = webEngine.getLocation();
					if (location.startsWith(Main.getTwitterSessionDAO().getCallbackUrl())) {
						String callbackURLWithTokens = location;
						browser.closeBrowser();
						try {
							verifyTokens(callbackURLWithTokens);
						} catch (AccessException | NetworkException e) {
							e.printStackTrace();
						}

					} else {
						// Mostrar ventana emergente "Couldn't connect to " + location
						
					}
				}

			}
		});
		
	}

	/**
	 * New login process 3rd step: Verify identity and sign in
	 * @return 
	 * 
	 * @throws AccessException
	 * @throws ConnectivityException
	 */
	public void verifyTokens(String callbackURL) throws AccessException, NetworkException {
		
		String oauthToken;
		String oauthVerifier;

		Map<String, String> urlMap = getQueryMap(callbackURL);
		oauthToken = urlMap.get("oauth_token");
		oauthVerifier = urlMap.get("oauth_verifier");

		if (oauthVerifier != null && ((requestToken.getToken().toString().equalsIgnoreCase(oauthToken)))) {
			webEngine.getLoadWorker().cancel();
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
			} catch (TwitterException e) {
				if (e.getStatusCode() == UNAUTHORIZED) {
					throw new AccessException("401: Unable to get the access token. Please check your credentials.", e);
				} else {
					throw new NetworkException(
							"You do not have internet connection. Please check it out before continue", e);
				}
			}
		}

		else { // TODO ¿¿??
			/*try {
				accessToken = twitter.getOAuthAccessToken();
			} catch (TwitterException e) {
				e.printStackTrace();
			}*/
		}

		try {
			dbu.saveLogin(twitter.verifyCredentials().getScreenName(), accessToken.getToken().toString(),
					accessToken.getTokenSecret().toString());
		} catch (DatabaseWriteException | TwitterException e) {
			e.printStackTrace();
		}
		Main.showSearch();

	}

	/**
	 * Retrieve session when the user has signed up before
	 * 
	 * @param twitter
	 * @param user
	 * @return
	 * @throws AccessException
	 * @throws ConnectivityException
	 * @throws DatabaseReadException
	 */
	public boolean retrieveSession(Twitter twitter, String user, DBUserDAO dbu)
			throws NetworkException, AccessException {

		this.dbu = dbu;

		String token = null;

		try {
			token = dbu.getUserData("access_token", user);
		} catch (DatabaseReadException e) {
			e.printStackTrace();
		}
		String secret = null;
		try {
			secret = dbu.getUserData("access_secret", user);
		} catch (DatabaseReadException e) {
			e.printStackTrace();
		}

		AccessToken at = new AccessToken(token, secret);
		twitter.setOAuthAccessToken(at);

		try {
			twitter.verifyCredentials().getId();
		} catch (TwitterException e1) {
			if (e1.getStatusCode() == UNAUTHORIZED) {
				throw new AccessException(e1.getErrorMessage(), e1);
			}
			throw new NetworkException("You do not have internet connection. Please check it out before continue", e1);
		}

		try {
			twitter.verifyCredentials().getScreenName();
		} catch (TwitterException e2) {
			throw new NetworkException("You do not have internet connection. Please check it out before continue", e2);
		}
		System.out.println("Showing search menu...");
		// main.showSearch();
		return true;
	}

	/**
	 * Retrieve parameters of a url (to obtain access tokens)
	 * 
	 * @param query
	 * @return
	 */
	public static Map<String, String> getQueryMap(String query) {
		String url = query.substring(query.indexOf("?") + 1);
		String[] params = url.split("&");
		Map<String, String> map = new HashMap<String, String>();

		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}
}