package application.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * General controller of progress bars of the application
 * @author Maria Cristina, github: cgg09
 *
 */

public class ProgressController {
	
	private Stage stage; 

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label processTitle;
	@FXML
	private Label processStatus;
	@FXML
	private Button detailsButton;
	/*@FXML
	private TextArea detailsArea;
	@FXML
	private AnchorPane detailsArea;
	*/
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProcessTitle(String title) {
		this.processTitle.setText(title);
	}
	
	public Label getProcessStatus() {
		return processStatus;
	}
	/*
	public AnchorPane getTextArea() {
		return detailsArea;
	}*/
	
	
	/**
	 * Initializes the controller class This method is automatically called after
	 * the fxml file has been loaded
	 */
	public void initialize() {
		progressBar.setProgress(0);
		processTitle.textProperty().unbind();
		progressBar.progressProperty().unbind();
		processStatus.textProperty().unbind();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		
	}

	public Stage getStage() {
		return stage;
	}
	
	//@FXML
	//private void detailsMenu() {
		//detailsArea.setDisable(false);
		/*if(detailsArea.isDisabled()) {
			detailsArea.setDisable(false);
			detailsButton.setText("Hide details");
		} else {
			detailsArea.setDisable(true);
			detailsButton.setText("Show details");
		}*/
		//detailsArea.setText("Hey");
		//System.out.println("Hey");
	//}
	
	/*public void disableDetails() {
		this.detailsArea.setDisable(true);
	}*/
}
