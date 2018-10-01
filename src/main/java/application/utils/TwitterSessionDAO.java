package application.utils;

import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * DAO class to manage all the information related to a Twitter session
 * @author Maria Cristina, github: cgg09
 *
 */

public class TwitterSessionDAO {

	private static TwitterSessionDAO instance;
	private Twitter twitter;
	private String callback_url;
	
	private TwitterSessionDAO() {
		
	}
	
	public static TwitterSessionDAO getInstance() {
		if(instance == null) {
			instance = new TwitterSessionDAO();
		}
		return instance;
	}
	
	
	public Twitter getTwitter() {
		return twitter;
	}
	
	public void setTwitterInstance(Twitter twitter) {
		this.twitter = twitter;
	}
	
	public String getCallbackUrl() {
		return callback_url;
	}
	
	/**
	 * Set consumer_key and consumer_secret credentials at the start of the connection
	 * @return twitter instance
	 */
	public Twitter startTwitterSession() {
		
		AppProperties appProps = new AppProperties();
		
		try {
			appProps.loadFile("client.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}

		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(appProps.getValue("consumer_key"));
		builder.setOAuthConsumerSecret(appProps.getValue("consumer_secret"));
		Configuration conf = builder.build();
		TwitterFactory factory = new TwitterFactory(conf); 
		Twitter twitter = factory.getInstance();
		setTwitterInstance(twitter);
		callback_url = appProps.getValue("base_callback_url");
		return twitter;
	}

}
