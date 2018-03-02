package tfg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder; //me gustaría acabar utilizando esta en vez de com.box.restclientv2 ...

import com.box.restclientv2.httpclientsupport.HttpClientURIBuilder;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import tfg.model.AppProperties;
import tfg.model.Browser;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Login {

	AppProperties appProps;// = new AppProperties();
	Twitter twitter;// = TwitterFactory.getSingleton();
	AccessToken accessToken = null;
	RequestToken requestToken = null;
	String callbackURLWithTokens;
	private Main main;
	
	
	public Login() {
		appProps = new AppProperties();
		twitter = TwitterFactory.getSingleton();
	}
/*	
	public Login getLogin() {
		return this;
	}
*/	
	/**
	 * Request an authorization to Twitter
	 */
	public void createRequest() {
		
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
			// Cambiar por ventana emergente, pedir al usuario que se conecte a Internet
			System.out.println("No pude conectarme con Twitter");
			System.exit(0);
		}
		
		if (null == accessToken) {
			main.showWebView(requestToken.getAuthorizationURL());
		}
	}
	
	/**
	 * Retrieve tokens from callback url
	 */
	public void retrieveTokens(Browser browser) {
		
		WebEngine webEngine = browser.getWebEngine();
		
		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<Worker.State>() {
					
					@Override
					public void changed(ObservableValue<? extends javafx.concurrent.Worker.State> observable,
							javafx.concurrent.Worker.State oldState, javafx.concurrent.Worker.State newState) {
						
						if( newState.equals(javafx.concurrent.Worker.State.FAILED) ) {
							String location = webEngine.getLocation();
							System.out.println("EVENT changed(), newState=FAILED, location=" + location);
							if( location.startsWith(appProps.getValue("base_callback_url")) ) {
								// Handle success:
								callbackURLWithTokens = location;
								// 1. close window
								// 2. call someone
								verifyTokens(callbackURLWithTokens);
								
							} else {
								// Mostrar ventana emergente "Couldn't connect to " + location
							}
						}

					}
				});

	}
	
	/**
	 * Verify identity and sign in
	 */
	public void verifyTokens(String callbackURL) {
		
		String oauth_token_base;
		String oauthToken;
		String oauthVerifier;
				
		try {
			oauthToken = getParameter(callbackURL, "oauth_token");
			oauthVerifier = getParameter(callbackURL, "oauth_verifier");
			System.out.println(requestToken.getToken().toString());
			System.out.println("oauth verifier: "+oauthVerifier);
//			System.out.println("oauth token base: "+oauth_token_base);
			System.out.println("oauth token: "+oauthToken);
//			boolean bool = oauthToken.equalsIgnoreCase(oauth_token_base);
//			System.out.println(bool);
			if(oauthVerifier!=null && ((requestToken.getToken().toString().equalsIgnoreCase(oauthToken)))) {
//				webEngine.getLoadWorker().cancel();
				System.out.println("ok");
				accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
				System.out.println("oks");
				appProps.storeData("user.properties", "access_token", accessToken.toString());
				main.showSearch();
			}

			else {
				accessToken = twitter.getOAuthAccessToken(requestToken);
			}

		} catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				System.out.println("Unable to get the access token.");
				//return false;
			} else {
				te.printStackTrace();
			}
		}		
	}
		
		
	
	
	
	
	
	
	public boolean getConnection() throws TwitterException, IOException {
		
		AppProperties appProps = new AppProperties();
		appProps.loadFile("client.properties");
		
		Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(appProps.getValue("consumer_key"), appProps.getValue("consumer_secret"));
		
		try {
			requestToken = twitter.getOAuthRequestToken(appProps.getValue("base_callback_url"));
		} catch( TwitterException e ) {
			// Cambiar por ventana emergente, pedir al usuario que se conecte a Internet
			System.out.println("No pude conectarme con Twitter");
			System.exit(0);
		}
		

		if (null == accessToken) {
//			System.out.println("Open the following URL and grant access to your account:");
//			System.out.println(requestToken.getAuthorizationURL());

//			main.getHostServices().showDocument(requestToken.getAuthorizationURL());
			main.showWebView(requestToken.getAuthorizationURL());
//---------------------------------------------------------------------			
			// QUE PASA SI accessToken != null !!!!

			Browser browser = new Browser();
			
			WebEngine webEngine = browser.getWebEngine(); 
			String finalCallbackURL = "";//browser.getCallbackURLWithTokens();
			System.out.println("Callback url: "+finalCallbackURL);

			try {
	
				String oauthVerifier = getParameter(requestToken.getAuthorizationURL(), "oauth_token");
				System.out.println("oauth verifier: "+oauthVerifier);
				if(oauthVerifier!=null) {
					webEngine.getLoadWorker().cancel();
					accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
				}

				else {
					accessToken = twitter.getOAuthAccessToken(requestToken);
				}

			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
					return false;
				} else {
					te.printStackTrace();
				}
			}		
		}
		//storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		System.out.println("Congrats!! :)");
		System.exit(0);
		return true;

	}
	
	/*
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
//			System.out.println("Param Name: "+param.getName());
//			System.out.println("Param Value: "+param.getValue());
			if (param.getName().equalsIgnoreCase(p)) {
				if(param.getValue()!=null) {
					return param.getValue();
				}
				return null;
			}
		}
		return null;
	}
	

	public String getCallbackURLWithTokens() {
		return callbackURLWithTokens;
	}

	public String getBaseCallbackURL() {
		// move to a configuration file
		return "http://127.0.0.1:8080/TFG/twitter_callback";
	}
	
	
	
	

	public void setMainApp(Main main) {
		this.main = main;
	}

}