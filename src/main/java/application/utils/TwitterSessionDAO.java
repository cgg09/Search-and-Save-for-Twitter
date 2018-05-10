package application.utils;

import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSessionDAO {

	private static TwitterSessionDAO instance;
	private AppProperties appProps;
	private Twitter twitter;
	
	private TwitterSessionDAO() {
		
	}
	
	public static TwitterSessionDAO getInstance() {
		if(instance == null) {
			instance = new TwitterSessionDAO();
		}
		return instance;
	}
	
	/**
	 * Set consumer_key and consumer_secret credentials at the start of the connection
	 * @return twitter instance
	 */
	public Twitter setTwitterInstance() {
		
		appProps = new AppProperties();
		
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
		this.twitter = twitter;
		return twitter;
	}
	
	public Twitter getTwitter() {
		return twitter;
	}
	
	
	
}
