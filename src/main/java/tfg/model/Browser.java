package tfg.model;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();

	public Browser() {

	}

	public Browser(String URL) {

		// load the web page
		webEngine.load(URL);

		// add the web view to the scene
		getChildren().add(browser);
			
	}


	public Browser getBrowser() {
		return this;
	}


	public WebEngine getWebEngine() {
		return webEngine;
	}
	

}
