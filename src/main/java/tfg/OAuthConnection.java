package tfg;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuthConnection {

	private Main main;

	public boolean getConnection() throws Exception{
		Twitter twitter = TwitterFactory.getSingleton();
		// a fichero de configuracion
		twitter.setOAuthConsumer("HHboBNBvNfUewAzChvrQ2tjAe", "6VCGaQUQdUGfO9o1Ec9wPJO81RTohFB10rbektUPEAqHZANCog");
		RequestToken requestToken = twitter.getOAuthRequestToken();
		System.out.println(requestToken.getToken());
		AccessToken accessToken = null;
		while (null == accessToken) {
			System.out.println(requestToken.getAuthorizationURL());
			main.showWebView(requestToken.getAuthorizationURL());
			//String pin = br.readLine();
			//try{
				//if(pin.length() > 0){
			//String pin = "4444"; 
					//accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				//}else{
					accessToken = twitter.getOAuthAccessToken(requestToken);
					//accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier); este es el que interesa!!!!
				//}
			//} catch (TwitterException te) {
				//if(401 == te.getStatusCode()){
					//System.out.println("Unable to get the access token.");
					//return false;
				//}else{
					//te.printStackTrace();
					//return false;
				//}
			}
//		}
		//user.setAccessToken(accessToken);
		//storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		//System.out.println("Congrats!! :)");
		//System.exit(0);
		return true;

	}
	/*
	private static void storeAccessToken(long l, AccessToken accessToken){
		//  store accessToken.getToken()
		// store accessToken.getTokenSecret()
	}*/

	public void setMainApp(Main main) {
		this.main = main;
	}
}