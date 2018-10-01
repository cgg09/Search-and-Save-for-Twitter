package application.view;

import java.util.Optional;

import application.Main;

import application.database.DBCollection;
/*import application.exceptions.NetworkException;
import application.exceptions.RateLimitException;
import application.exceptions.AccessException;*/
import application.exceptions.DatabaseReadException;
import application.tasks.SearchTask;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import twitter4j.Twitter;

/**
 * Controller of the new search menu of the application
 * @author Maria Cristina, github: cgg09
 *
 */

public class NewHistoricDialogController {

	Twitter twitter;

	@FXML
	private TextField userQuery;
	@FXML
	private TextField userQuery1;
	@FXML
	private TextField userQuery2;
	@FXML
	private TextField userQuery3;
	@FXML
	private Button searchButton;
	@FXML
	private GridPane queriesPane = new GridPane();

	private DBCollection collection;
	private Stage dialogStage;
	private boolean okClicked;
	private HistoricViewController historic;

	int total;
	boolean repeat = false;

	/**
	 * The constructor, called before the initialize() method
	 */
	public NewHistoricDialogController() {


	}

	/**
	 * Initializes the controller class This method is automatically called after
	 * the fxml file has been loaded
	 */
	public void initialize() {

		searchButton.setOnAction(e-> {
			dialogStage.close();
			ProgressController progress = Main.showProgressBar("Downloading tweets");

			String q = userQuery.getText();
			if(!userQuery1.getText().isEmpty()) {
				q += " OR "+userQuery1.getText();
			}
			if(!userQuery2.getText().isEmpty()) {
				q += " OR "+userQuery2.getText();
			}
			if(!userQuery3.getText().isEmpty()) {
				q += " OR "+userQuery3.getText();
			}

			if (checkSearch(q)) {
				q = null;
				q = collection.getQuery();
			}

			Task<Void> newSearchTask = new SearchTask(collection,q);

			progress.getProcessStatus().textProperty().set("New search");
			progress.getProcessStatus().textProperty().bind(newSearchTask.messageProperty());
			progress.getProgressBar().progressProperty().bind(newSearchTask.progressProperty());
			progress.getProcessStatus().textProperty().bind(newSearchTask.messageProperty());

			newSearchTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
					new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					okClicked = true;
					progress.getStage().close();
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("NEW SEARCH FINISHED");
					alert.setHeaderText("Tweets downloaded");
					if (collection.getDownloaded() != 0) {
						alert.setContentText("Success! You have downloaded " + collection.getDownloaded() + " tweets");
					} else {
						alert.setContentText("You haven't downloaded any tweet, try next time!");
					}
					alert.showAndWait();
					historic.updateViews();
				}							
			});
			// FIXME I am not able to show the warnings in case of error
			newSearchTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, 
					new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					System.out.println(event.getEventType());
					System.out.println("FAILED!!! :(");
				}
			});
			new Thread(newSearchTask).start();
		});

	}

	/**
	 * Checks if the search has been previously done
	 * @param q (query)
	 * @return if the search is new or not
	 */
	private boolean checkSearch(String q) {

		boolean repeat = false;

		Integer col = null;

		try {
			col = collection.checkQuery(q);
		} catch (DatabaseReadException e2) {
			e2.printStackTrace();
		}

		if (col != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("REPEAT SEARCH");
			alert.setHeaderText("Repeating search");
			alert.setContentText(
					"Wait! This query is for a previous search. Do you want to add the tweets downloaded to the old search?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				repeat = true;
				collection.setId(col);

				try {
					collection.updateCollection();
				} catch (DatabaseReadException e) {
					e.printStackTrace();
				}
				collection.setRepeated(repeat);
				return true;
			} else {
				collection.setRepeated(repeat);
				return false;
			}
		} else {
			collection.setRepeated(repeat);
			return false;
		}
	}

	public void setDialogStage(Stage stage) {
		dialogStage = stage;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	public void setCollection(DBCollection c) {
		this.collection = c;
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * The user cancels the search
	 */
	@FXML
	private void handleCancel() {
		collection = null;
		dialogStage.close();
	}

	public void setHistoricView(HistoricViewController historicViewController) {
		this.historic = historicViewController;
	}

}
