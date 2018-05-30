package application.view;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ProgressController {
	private Task<Object> task;
	
	private Stage stage;

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label processTitle;
	@FXML
	private Label processStatus;
	@FXML
	private Button detailsButton;
	@FXML
	private TextArea detailsArea;
	
	public Task<Object> getTask() {
		return task;
	}
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProcessTitle(String title) {
		this.processTitle.setText(title);
	}
	
	public Label getProcessStatus() {
		return processStatus;
	}
	
	public TextArea getTextArea() {
		return detailsArea;
	}
	
	public void initialize() {
		progressBar.setProgress(0);
		processTitle.textProperty().unbind();
		progressBar.progressProperty().unbind();
		processStatus.textProperty().unbind();
		//detailsArea.setDisable(true);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		
	}

	public Stage getStage() {
		return stage;
	}
	
	@FXML
	private void detailsMenu() {
		/*if(detailsArea.isDisabled()) {
			detailsArea.setDisable(false);
			detailsButton.setText("Hide details");
		} else {
			detailsArea.setDisable(true);
			detailsButton.setText("Show details");
		}*/
		detailsArea.setText("Hey");
		//System.out.println("Hey");
	}
}
