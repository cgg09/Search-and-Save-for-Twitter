package application.exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DataNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataNotFoundException(String message) {
		super(message);
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("DATA NOT FOUND");
		alert.setHeaderText("Data was not found");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/*
	 * public String toString() { return
	 * "This information was not found in the database."; }
	 */

}
