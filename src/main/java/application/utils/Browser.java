package application.utils;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * General class to open a browser in a webview
 * @author Maria Cristina, github: cgg09
 *
 */

public class Browser extends Region {

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();
	private Stage currentStage;

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
	
	public void setStage(Stage stage) {
		currentStage = stage;
	}
	
	public void closeBrowser() {
		currentStage.close();
	}
	

}
