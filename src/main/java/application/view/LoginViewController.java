package application.view;

import java.util.List;
import java.util.Vector;

import application.Main;
import application.database.DBUserDAO;
import application.exceptions.AccessException;
import application.exceptions.NetworkException;
import application.tasks.LoginTask;
import application.tasks.SignUpTask;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import application.view.ProgressController;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoginViewController {

	@FXML
	private Button signUp;
	@FXML
	private MenuButton loginButton;
	private Stage currentStage;

	/**
	 * The constructor, called before the initialize() method
	 */
	public LoginViewController() {

	}

	/**
	 * Initializes the controller class This method is automatically called after
	 * the fxml file has been loaded
	 * 
	 * @throws DatabaseReadException
	 */
	@FXML
	public void initialize() {


		
		// get user list
		Main.setDBUserDAO(DBUserDAO.getInstance());
		List<String> users = new Vector<String>();
		try {
			users = Main.getDBUserDAO().getUsers();
		} catch (DatabaseReadException | DataNotFoundException e2) {
			e2.printStackTrace();
		}
		
		signUp.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				try {
					handleSignUp();
				} catch (NetworkException | AccessException e2) {
					e2.printStackTrace();
				}
			}
			
		});
		
		// show users' list to login
		if (!users.isEmpty()) {
			for (String u : users) {
				MenuItem m = new MenuItem(u);
				m.setOnAction(e -> {
					MenuItem source = (MenuItem) e.getSource();
					currentStage.hide();
					ProgressController progress = Main.showProgressBar("Login");
					progress.getStage().getScene().setCursor(Cursor.WAIT);

					Task<Void> loginTask = new LoginTask(source.getText());
					progress.getProcessStatus().textProperty().set("Login user");
					progress.getProcessStatus().textProperty().bind(loginTask.messageProperty());
					progress.getProgressBar().progressProperty().bind(loginTask.progressProperty());
					progress.getProcessStatus().textProperty().bind(loginTask.messageProperty());
					
					loginTask.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							
							/**
							 *  TODO: show progress messages:
							 *  Creating twitter session
							 *  		|
							 *  		v
							 *  Retrieving user session (getting db info/connecting to twitter/verifying credentials)
							 *  -------------------------------------------------------------------------------------
							 *  Loading view
							 *  Loading info user (collections)
							 */
						}

					});
					
					loginTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {

						@Override
						public void handle(WorkerStateEvent event) {
							System.out.println(event.getSource().getException());
							
						}
						
					});

					loginTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
							new EventHandler<WorkerStateEvent>() {
								@Override
								public void handle(WorkerStateEvent event) {
									Main.showSearch();
									progress.getStage().getScene().setCursor(Cursor.DEFAULT);
									progress.getStage().close();
									
								}
							});
					new Thread(loginTask).start();

				});
				loginButton.getItems().add(m);
			}
		} else {
			loginButton.setDisable(true);
		}
	}

	public void setStage(Stage stage) {
		currentStage = stage;
		
	}

	/**
	 * When the user clicks the login button
	 * 
	 * @throws ConnectivityException
	 * @throws AccessException
	 * @throws Exception
	 */
	@FXML
	private void handleSignUp() throws NetworkException, AccessException {
		currentStage.hide();
		ProgressController progress = Main.showProgressBar("New login");
		progress.getStage().getScene().setCursor(Cursor.WAIT);
		Task<Void> singUpTask = new SignUpTask();
		progress.getProcessStatus().textProperty().set("New user user");
		progress.getProcessStatus().textProperty().bind(singUpTask.messageProperty());
		progress.getProgressBar().progressProperty().bind(singUpTask.progressProperty());
		progress.getProcessStatus().textProperty().bind(singUpTask.messageProperty());
		
		singUpTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println(event.getSource().getException());
				//System.out.println(event.getTarget().toString());
				// NETWORK -- ACCESS FAILURES
			}
			
		});

		singUpTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
				new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {						
						Main.showWebView(Main.getLogin().getRequestToken().getAuthorizationURL());
						progress.getStage().getScene().setCursor(Cursor.DEFAULT);
						progress.getStage().close();						
					}
				});
		new Thread(singUpTask).start();
				
	}

}
