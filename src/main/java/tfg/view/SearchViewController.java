package tfg.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tfg.Main;

public class SearchViewController {

	@FXML
	private Label username;
	
	private Main main;
	
	public SearchViewController() {
		
	}
	
	@FXML
	public void initialize() {
//		System.out.println(main.getUser().getUsername().toString());
		
	}
	
	public void setUsername(String u) {
		username.setText(u);
//		String user = main.getUser().getUsername();
	}
	
	/**
	 * Is called by the main application to give a reference back to itself
	 * 
	 * @param main
	 */
	public void setMainApp(Main main) {
		this.main = main;
	}

}
