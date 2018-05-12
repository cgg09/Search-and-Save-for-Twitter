package application.view;

import application.Main;
import application.database.DBCollection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class SearchViewController extends AnchorPane {

	@FXML
	private Label username;
	private Main main;

	public SearchViewController() {

	}

	@FXML
	public void initialize() {
		HistoricViewController.init(this); // necesario para poder hacer las b�squedas
		//System.out.println("Hi Search");
	}

	public String getUsername() {
		return username.getText();
	}

	public void setUsername(String u) {
		username.setText(u);
	}

	public boolean newSearch(DBCollection c) {

		boolean okClicked = false;
		if (c.getType().equals("Historic")) {
			okClicked = main.showNewHistoricSearch(c);			
			
		} /*else if (c.getType().equals("Live")) {
			// okClicked = main.showNewLiveSearch(c);
		}*/
		return okClicked;

	}

	public void setMainApp(Main main) {
		this.main = main;
		//main.getPrimaryStage().getScene().setRoot(filterMenu);
	}

	public Main getMain() {
		return main;
	}

}
