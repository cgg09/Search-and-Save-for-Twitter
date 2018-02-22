package tfg;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import tfg.model.User;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuthConnection {

	private Main main;

	public boolean getConnection(User user) throws Exception{
		Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("HHboBNBvNfUewAzChvrQ2tjAe", "6VCGaQUQdUGfO9o1Ec9wPJO81RTohFB10rbektUPEAqHZANCog");
		RequestToken requestToken = twitter.getOAuthRequestToken();
		System.out.println(requestToken.getToken());
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			main.showWebView(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN(if available) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try{
				if(pin.length() > 0){
					accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				}else{
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if(401 == te.getStatusCode()){
					System.out.println("Unable to get the access token.");
					return false;
				}else{
					te.printStackTrace();
					return false;
				}
			}
		}
		user.setAccessToken(accessToken);
		storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		System.out.println("Congrats!! :)");
		//System.exit(0);
		return true;

	}
	private static void storeAccessToken(long l, AccessToken accessToken){
		//  store accessToken.getToken()
		// store accessToken.getTokenSecret()

	}

	public void setMainApp(Main main) {
		this.main = main;
	}
}