package application.view;

import java.util.Optional;

import application.Main;

import application.database.DBCollection;
import application.exceptions.NetworkException;
import application.exceptions.RateLimitException;
import application.exceptions.AccessException;
import application.exceptions.DatabaseReadException;
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
	
	public NewHistoricDialogController() {
		

	}

	public void initialize() {
		
		

		searchButton.setOnAction(e-> {
			dialogStage.close();
			ProgressController progress = Main.showProgressBar("Downloading tweets");
			Task<Boolean> newSearch = new Task<Boolean>() {

				@Override
				protected Boolean call() throws Exception {
					
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
					
					System.out.println("Final query: "+q);
					
					
					boolean r = checkSearch(q);
					boolean d = false;

					if (r) {
						q = null;
						q = collection.getQuery();
					}
					
					try {
						d = collection.manageSearch(q);
					} catch (AccessException | RateLimitException | NetworkException e1) {
						e1.printStackTrace();
					}
					return d;
					
				}
				
			};
			
			progress.getProcessStatus().textProperty().set("New search");
			progress.getProcessStatus().textProperty().bind(newSearch.messageProperty());
			progress.getProgressBar().progressProperty().bind(newSearch.progressProperty());
			progress.getProcessStatus().textProperty().bind(newSearch.messageProperty());
			newSearch.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, 
					new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							int downloaded = collection.getDownloaded();
							//System.out.println(downloaded);
							progress.getProcessStatus().textProperty().unbind();
							progress.getProcessStatus().setText("Downloaded tweets: " + downloaded);
						}
			});
			newSearch.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
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
							//System.out.println("Finish...");
							//HistoricViewController historic = Main.getPrimaryStage().getClass();
							historic.updateViews();
						}							
			});
			newSearch.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, 
					new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							System.out.println("FAILED!!! :(");
						}
			});
			new Thread(newSearch).start();
		});

	}

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
		/*
		 * if(!collection.getQuery().isEmpty()) {
		 * userQuery.setText(collection.getQuery()); }
		 */ // esto era para el método anterior de repeatSearch
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleCancel() {
		collection = null;
		dialogStage.close();
	}
	
	public void setHistoricView(HistoricViewController historicViewController) {
		this.historic = historicViewController;
	}

}
