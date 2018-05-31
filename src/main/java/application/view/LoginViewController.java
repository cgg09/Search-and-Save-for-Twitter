package application.view;

import java.util.List;
import java.util.Vector;

import application.Main;
import application.database.DBUserDAO;
import application.exceptions.AccessException;
import application.exceptions.NetworkException;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class LoginViewController {

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
		// show users' list to login
		if (!users.isEmpty()) {
			for (String u : users) {
				MenuItem m = new MenuItem(u);
				m.setOnAction(e -> {
					MenuItem source = (MenuItem) e.getSource();
					currentStage.hide();
					ProgressController progress = Main.showProgressBar("Login");
					progress.getStage().getScene().setCursor(Cursor.WAIT);
					Task<Boolean> login = new Task<Boolean>() {

						@Override
						protected Boolean call() throws Exception {
							boolean log = false;
							//updateProgress(0,100);
							//updateMessage(progress.getProcessStatus().getText());
							try {
								log = Main.manageFastLogin(source.getText());
							} catch (NetworkException e1) {
								e1.printStackTrace();
							}
							//updateProgress(50,100);
							//updateMessage(progress.getProcessStatus().getText());
							System.out.println("Sucess?: "+log);
							return log;
						}

					};
					progress.getProcessStatus().textProperty().set("Login user");
					progress.getProcessStatus().textProperty().bind(login.messageProperty());
					progress.getProgressBar().progressProperty().bind(login.progressProperty());
					progress.getProcessStatus().textProperty().bind(login.messageProperty());
					
					login.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, new EventHandler<WorkerStateEvent>() {
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
					
					login.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {

						@Override
						public void handle(WorkerStateEvent event) {
							System.out.println(event.getSource().getException());
							//System.out.println(event.getTarget().toString());
						}
						
					});

					login.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
							new EventHandler<WorkerStateEvent>() {
								@Override
								public void handle(WorkerStateEvent event) {
									System.out.println("Great!!");
									
									Main.showSearch();
									//updateProgress(100,100);
									progress.getStage().getScene().setCursor(Cursor.DEFAULT);
									progress.getStage().close();
									
								}
							});
					new Thread(login).start();

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
		
		
		//currentStage.close();

		currentStage.hide();
		ProgressController progress = Main.showProgressBar("New login");
		progress.getStage().getScene().setCursor(Cursor.WAIT);
		Task<Boolean> newLogin = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				boolean log = false;
				//updateProgress(0,100);
				//updateMessage(progress.getProcessStatus().getText());
				try {
					log = Main.manageNewLogin();
					//Main.getLogin().getSuccess();
				} catch (NetworkException e1) {
					e1.printStackTrace();
				}
				//updateProgress(50,100);
				//updateMessage(progress.getProcessStatus().getText());
				System.out.println("Sucess?: "+log);
				return log;
			}

		};
		progress.getProcessStatus().textProperty().set("Login user");
		progress.getProcessStatus().textProperty().bind(newLogin.messageProperty());
		progress.getProgressBar().progressProperty().bind(newLogin.progressProperty());
		progress.getProcessStatus().textProperty().bind(newLogin.messageProperty());
		
		newLogin.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, new EventHandler<WorkerStateEvent>() {
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
		
		newLogin.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println(event.getSource().getException());
				//System.out.println(event.getTarget().toString());
			}
			
		});

		newLogin.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
				new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						System.out.println("Great!!");
						
						//Main.showSearch();
						Main.showWebView(Main.getLogin().getRequestToken().getAuthorizationURL());
						//updateProgress(100,100);
						progress.getStage().getScene().setCursor(Cursor.DEFAULT);
						progress.getStage().close();
						
					}
				});
		new Thread(newLogin).start();
		
		
		
		
		
		
		
	
		
	}

}
