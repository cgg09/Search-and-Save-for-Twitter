package application.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
	
	Properties props = new Properties();
	
	public AppProperties() {
		
	}

	public void loadFile(String path) throws IOException {
		InputStream file = getClass().getClassLoader().getResourceAsStream(path);
		props.load(file);
	}
	
	public String getValue(String key) {
		return props.getProperty(key);
	}
	
	public void storeData(String key, String value) {
		props.setProperty(key, value);
	}
}
