package daos;

import tfg.Login;

public class LoginDAO {

	private static LoginDAO instance;
	
	
	private LoginDAO() {
		
	}
	
	public static LoginDAO getInstance() {
		if(instance == null) instance = new LoginDAO();
		return instance;
	}
}
