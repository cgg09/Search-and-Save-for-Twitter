package tfg.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class AppProperties {
	
	Properties props = new Properties();
	
	public AppProperties() {
		
	}

	public void loadFile(String path) throws IOException {
		InputStream file = getClass().getClassLoader().getResourceAsStream(path);
		props.load(file);
	}
	
	public void storeData(String path, String key, String value) {
//		OutputStream file = getClass().getClassLoader().getResourceAsStream(path);
//		String newAppConfigPropertiesFile = "user.properties";
		try {
			loadFile(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//appProps.store(new FileWriter(newAppConfigPropertiesFile), "store to properties file");

		props.setProperty(key, value);
	}
	
	public String getValue(String key) {
		return props.getProperty(key);
	}	

	// store info in a properties file

}
