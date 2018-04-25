package application.view;

import application.Main;
import application.database.DBCollection;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

public class SearchViewController extends AnchorPane {

	@FXML
	private Label username;
	@FXML
	private ChoiceBox<String> filterMenu;
	@FXML
	private Label show;
	private Main main;

	public SearchViewController() {

	}

	@FXML
	public void initialize() {
		
		// initialize filter button to "last 200 tweets"
		filterMenu.getItems().addAll("Last 200 tweets","All tweets (except RTs)","All tweets");

		// change of selection in filter button "show"
		filterMenu.getSelectionModel().selectedIndexProperty()
				.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
					System.out.println("You clicked " + filterMenu.getItems().get((Integer)newValue)); // correcto
				});
		

		
		HistoricViewController.init(this);
		// LiveViewController.init(this);
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
