package application;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URIBuilder; //me gustaría acabar utilizando esta en vez de com.box.restclientv2 ...

import com.box.restclientv2.httpclientsupport.HttpClientURIBuilder;

import application.model.AppProperties;
import application.model.Browser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Login {

	AppProperties appProps = new AppProperties();
	Twitter twitter;
	AccessToken accessToken = null;
	RequestToken requestToken = null;
	WebEngine webEngine;
	private Main main;


	public Login() {

	}

	/**
	 * New login process
	 * 1st step: Request an authorization to Twitter
	 */
	public void createRequest(Twitter twitter) {

		this.twitter = twitter;
		// read client properties file

		try {
			appProps.loadFile("client.properties");
		} catch (IOException e) {
			// saltar error al cargar datos
			System.out.println("archivo cargado incorrectamente");
			e.printStackTrace();
		}

		// create Twitter request

		twitter.setOAuthConsumer(appProps.getValue("consumer_key"), appProps.getValue("consumer_secret"));

		try {
			requestToken = twitter.getOAuthRequestToken(appProps.getValue("base_callback_url"));
		} catch( TwitterException e ) {
			System.out.println("No pude conectarme con Twitter"); // pedir al usuario que se conecte a Internet (ventana emergente)
			System.exit(0);
		}

		if (null == accessToken) {
			main.showWebView(requestToken.getAuthorizationURL());
		}
	}

	/**
	 * New login process
	 * 2n step: Retrieve tokens from callback url
	 */
	public void retrieveTokens(Browser browser) {

		webEngine = browser.getWebEngine();

		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<Worker.State>() {

					@Override
					public void changed(ObservableValue<? extends javafx.concurrent.Worker.State> observable,
							javafx.concurrent.Worker.State oldState, javafx.concurrent.Worker.State newState) {

						if( newState.equals(javafx.concurrent.Worker.State.FAILED) ) {
							String location = webEngine.getLocation();
							if( location.startsWith(appProps.getValue("base_callback_url")) ) {
								String callbackURLWithTokens = location;
								browser.closeBrowser();
								verifyTokens(callbackURLWithTokens);

							} else {
								// Mostrar ventana emergente "Couldn't connect to " + location
							}
						}

					}
				});

	}

	/**
	 * New login process
	 * 3rd step: Verify identity and sign in
	 */
	public void verifyTokens(String callbackURL) {

		String oauthToken;
		String oauthVerifier;

		try {
			oauthToken = getParameter(callbackURL, "oauth_token");
			oauthVerifier = getParameter(callbackURL, "oauth_verifier");
			if(oauthVerifier!=null && ((requestToken.getToken().toString().equalsIgnoreCase(oauthToken)))) {
				webEngine.getLoadWorker().cancel();
				accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
			}

			else {
				accessToken = twitter.getOAuthAccessToken();
			}

		} catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				System.out.println("Unable to get the access token.");
				//return false;
			} else {
				te.printStackTrace();
			}
		}
		
		try {
			main.getUser().setUsername(twitter.verifyCredentials().getScreenName());
			DB.saveLogin(main.getUser().getUsername(), accessToken.getToken().toString(), accessToken.getTokenSecret().toString());
			main.showSearch();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	/**
	 * Retrieve session when the user has signed up before
	 * @param twitter
	 * @param user
	 */
	public void retrieveSession(Twitter twitter, String user) {

		try {
			appProps.loadFile("client.properties");
		} catch (IOException e) {
			// saltar error al cargar datos
			System.out.println("archivo cargado incorrectamente");
			e.printStackTrace();
		}
		
	    twitter.setOAuthConsumer(appProps.getValue("consumer_key"), appProps.getValue("consumer_secret"));
	    String token = DB.getUserData("access_token",user);
	    String secret = DB.getUserData("access_secret",user);
	    AccessToken at = new AccessToken(token,secret);
	    twitter.setOAuthAccessToken(at);
	    
	    // primero verificar conexión así --> !!! Aquí se controlarán tokens expirados, etc
	    
	    try {
			twitter.verifyCredentials().getId();
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    try {
	    	main.getUser().setUsername(twitter.verifyCredentials().getScreenName());
			main.showSearch();
		} catch (TwitterException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    
	}


	/**
	 * Retrieve parameters from a URL
	 */

	private String getParameter(final String url, final String p) {

		HttpClientURIBuilder uri;
		try {
			uri = new HttpClientURIBuilder(url);
		}
		catch (URISyntaxException e) {
			return null;
		}

		List<NameValuePair> query = uri.getQueryParams();
		for (NameValuePair param : query) {
			if (param.getName().equalsIgnoreCase(p)) {
				if(param.getValue()!=null) {
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