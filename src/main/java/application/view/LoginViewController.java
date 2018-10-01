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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import twitter4j.TwitterException;

/**
 * Controller of the login view of the application
 * @author Maria Cristina, github: cgg09
 *
 */

public class LoginViewController {

	@FXML
	private Button signUp;
	@FXML
	private MenuButton loginButton;
	private Stage currentStage;
	private int UNAUTHORIZED = 401;

	/**
	 * The constructor, called before the initialize() method
	 */
	public LoginViewController() {

	}

	/**
	 * Initializes the controller class This method is automatically called after
	 * the fxml file has been loaded
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
					
					
					// FIXME I am not able to show the warnings in case of error
					
					/*loginTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
					      @Override public void handle(WorkerStateEvent t) {
					        Object ee = loginTask.getException();
					        TwitterException e = (TwitterException) ee;
					        System.out.println("Exception: "+e.getMessage());
					        if(((TwitterException) e).getStatusCode() == UNAUTHORIZED) {
					        	Alert alert = new Alert(AlertType.WARNING);
					    		alert.setTitle("ACCESS FAILURE");
					    		alert.setHeaderText("Access error");
					    		alert.setContentText(((TwitterException) e).getErrorMessage()+" Please check out your Twitter settings account.");
					    		alert.showAndWait();*/
								/*try {
									throw new AccessException(e.getErrorMessage(), e);
								} catch (AccessException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							try {
								throw new NetworkException("You do not have internet connection. Please check it out before continue", e);
							} catch (NetworkException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();*/
							/*} else {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("CONNECTIVITY FAILURE");
								alert.setHeaderText("Internet connection error");
								alert.setContentText("You do not have internet connection. Please check it out before continue");
								alert.showAndWait();
							}
					        //loginTask.setText(ouch.getClass().getName() + " -> " + ouch.getMessage());
					      }
					    });
			
					*/
					/*loginTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {

						@Override
						public void handle(WorkerStateEvent event) {
							System.out.println("Heee"+event.getSource().getException());
							System.out.println(loginTask.exceptionProperty().get());
							loginTask.exceptionProperty().addListener((observableValue, oldValue, newValue)->{
								TwitterException e = (TwitterException) newValue;
								System.out.println("Twitter exception");
								if(e.getStatusCode() == UNAUTHORIZED) {
						        	Alert alert = new Alert(AlertType.WARNING);
						    		alert.setTitle("ACCESS FAILURE");
						    		alert.setHeaderText("Access error");
						    		alert.setContentText(e.getErrorMessage()+" Please check out your Twitter settings account.");
						    		alert.showAndWait();
								} else {
									Alert alert = new Alert(AlertType.WARNING);
									alert.setTitle("CONNECTIVITY FAILURE");
									alert.setHeaderText("Internet connection error");
									alert.setContentText("You do not have internet connection. Please check it out before continue");
									alert.showAndWait();
								}
								
							});
						}
						
					});*/

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
	 * The user clicks the new login button
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
		
		// FIXME I am not able to show the warnings in case of error
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
