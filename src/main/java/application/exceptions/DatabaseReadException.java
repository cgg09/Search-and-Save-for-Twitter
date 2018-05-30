package application.exceptions;

import java.sql.SQLException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DatabaseReadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseReadException(String message, SQLException e) {
		super(message);
		e.printStackTrace();
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("DATA READ FAILURE");
		alert.setHeaderText("Error while reading data");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/*
	 * public String toString() { return "There was an error reading the database.";
	 * }
	 */

}
