package application;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URIBuilder; //me gustar�a acabar utilizando esta en vez de com.box.restclientv2 ...

import com.box.restclientv2.httpclientsupport.HttpClientURIBuilder;

import application.database.DBUserDAO;
import application.exceptions.AccessException;
import application.exceptions.ConnectivityException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;
import application.utils.AppProperties;
import application.utils.Browser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Login {

	private AppProperties appProps = new AppProperties();
	private Twitter twitter;
	private AccessToken accessToken = null;
	private RequestToken requestToken = null;
	private WebEngine webEngine;
	private Main main;
	private DBUserDAO dbu;

	public Login() {

	}
	
	/**
	 * Sets consumer_key and consumer_secret credentials at the start of the connection
	 * @return
	 */
	public Twitter setTwitterInstance() {
				
		try {
			appProps.loadFile("client.properties");
		} catch (IOException e) {
			// saltar error al cargar datos
			System.out.println("archivo cargado incorrectamente");
			e.printStackTrace();
		}

		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(appProps.getValue("consumer_key"));
		builder.setOAuthConsumerSecret(appProps.getValue("consumer_secret"));
		Configuration conf = builder.build();
		TwitterFactory factory = new TwitterFactory(conf); 
		Twitter twitter = factory.getInstance();
		
		return twitter;
	}

	/**
	 * New login process 1st step: Request an authorization to Twitter
	 * 
	 * @throws ConnectivityException
	 * @throws AccessException
	 */
	public void createRequest(Twitter twitter, DBUserDAO dbu) throws ConnectivityException, AccessException {

		this.dbu = dbu;
		this.twitter = twitter;

		try {
			requestToken = twitter.getOAuthRequestToken(appProps.getValue("base_callback_url"));
		} catch (TwitterException e) {
			if (401 == e.getStatusCode()) {
				throw new AccessException("401: Unable to get the access token. Please check your credentials.");
			} else {
				throw new ConnectivityException();
			}
		}

		if (null == accessToken) {
			main.showWebView(requestToken.getAuthorizationURL());
		}
	}

	/**
	 * New login process 2n step: Retrieve tokens from callback url
	 */
	public void retrieveTokens(Browser browser) {

		webEngine = browser.getWebEngine();

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

			@Override
			public void changed(ObservableValue<? extends javafx.concurrent.Worker.State> observable,
					javafx.concurrent.Worker.State oldState, javafx.concurrent.Worker.State newState) {

				if (newState.equals(javafx.concurrent.Worker.State.FAILED)) {
					String location = webEngine.getLocation();
					if (location.startsWith(appProps.getValue("base_callback_url"))) {
						String callbackURLWithTokens = location;
						browser.closeBrowser();
						try {
							verifyTokens(callbackURLWithTokens);
						} catch (AccessException | ConnectivityException e) {
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
	 * @throws AccessException 
	 * @throws ConnectivityException 
	 */
	public void verifyTokens(String callbackURL) throws AccessException, ConnectivityException {

		String oauthToken;
		String oauthVerifier;

		oauthToken = getParameter(callbackURL, "oauth_token");
		oauthVerifier = getParameter(callbackURL, "oauth_verifier");
		if (oauthVerifier != null && ((requestToken.getToken().toString().equalsIgnoreCase(oauthToken)))) {
			webEngine.getLoadWorker().cancel();
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
			} catch (TwitterException e) {
				if (401 == e.getStatusCode()) {
					throw new AccessException("401: Unable to get the access token. Please check your credentials.");
				} else {
					throw new ConnectivityException();
				}
			}
		}

		else {
			try {
				accessToken = twitter.getOAuthAccessToken();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			main.getUser().setUsername(twitter.verifyCredentials().getScreenName());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dbu.saveLogin(main.getUser().getUsername(), accessToken.getToken().toString(),
					accessToken.getTokenSecret().toString());
		} catch (DatabaseWriteException e) {
			e.printStackTrace();
		}
		main.showSearch();

	}

	/**
	 * Retrieve session when the user has signed up before
	 * 
	 * @param twitter
	 * @param user
	 * @throws ConnectivityException
	 * @throws DatabaseReadException
	 */
	public void retrieveSession(Twitter twitter, String user, DBUserDAO dbu) throws ConnectivityException {
		
		this.dbu = dbu;

		String token = null;
		
		try {
			token = dbu.getUserData("access_token", user);
		} catch (DatabaseReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String secret = null;
		try {
			secret = dbu.getUserData("access_secret", user);
		} catch (DatabaseReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AccessToken at = new AccessToken(token, secret);
		twitter.setOAuthAccessToken(at);

		try {
			twitter.verifyCredentials().getId();
		} catch (TwitterException e1) { // FIXME connectivity exception / Access Exceptions (acceso revocado..., etc) ?
			throw new ConnectivityException();
		}

		try {
			main.getUser().setUsername(twitter.verifyCredentials().getScreenName());
		} catch (TwitterException e2) { // FIXME connectivity exception
			throw new ConnectivityException();
		}
		main.showSearch();
	}

	/**
	 * Retrieve parameters from a URL
	 */

	private String getParameter(final String url, final String p) {

		HttpClientURIBuilder uri;
		try {
			uri = new HttpClientURIBuilder(url);
		} catch (URISyntaxException e) {
			return null;
		}

		List<NameValuePair> query = uri.getQueryParams();
		for (NameValuePair param : query) {
			if (param.getName().equalsIgnoreCase(p)) {
				if (param.getValue() != null) {
					return param.getValue();
				}
				return null;
			}
		}
		return null;
	}

	public void setMainApp(Main main) {
		this.main = main;
	}

}