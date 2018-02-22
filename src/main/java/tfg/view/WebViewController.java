package tfg.view;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tfg.Main;

public class WebViewController extends Region {
	
	@FXML
	private WebView browser = new WebView();
	private WebEngine webEngine = browser.getEngine();
	private Main main;
	private Stage webStage;
	
	public WebViewController() {
		
	}
	
	public WebViewController(String url) {
		this.webEngine.load(url);
		getChildren().add(browser);
	}

	/**
	 * Initializes the controller class
	 * This method is automatically called after the fxml file has been loaded
	 */
	@FXML
	public void initialize() {
//		this.webEngine.load(url);
		//link.setOnAction(this::handleLink);
		
	}

	
	
	public void setMainApp(Main main) {
		this.main = main;
	}

	public void setDialogStage(Stage webStage) {
		this.webStage = webStage;
	
	}
	
	

}
