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
		HistoricViewController.init(this); // necesario para poder hacer las búsquedas
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

		if (c.getType() == "Historic") {
			okClicked = main.showNewHistoricSearch(c);

		} else if (c.getType() == "Live") {
			// okClicked = main.showNewLiveSearch(c);
		}
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
