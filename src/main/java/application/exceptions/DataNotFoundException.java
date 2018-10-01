package application.exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Class to manage exceptions related to data not found in the database  
 * @author Maria Cristina, github: cgg09
 * FIXME not sure if this class is necessary
 */

public class DataNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataNotFoundException(String message) {
		super(message);
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("DATA NOT FOUND");
		alert.setHeaderText("Data was not found");
		alert.setContentText(message);
		alert.showAndWait();
	}

}
