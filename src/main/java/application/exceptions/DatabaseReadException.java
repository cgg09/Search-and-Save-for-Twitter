package application.exceptions;

import java.sql.SQLException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


/**
 * Class to manage exceptions related to reading errors of the database 
 * @author Maria Cristina, github: cgg09
 *
 */

public class DatabaseReadException extends Exception {

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

}
