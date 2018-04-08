package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.Main;

public class DBUserDAO {

	public static DBUserDAO instance;
	private String user;
	private Connection c;

	private DBUserDAO() {
		c = Main.getDatabaseDAO().getConnection();
	}
	
	public static DBUserDAO getInstance() {
		if(instance == null) {
			instance = new DBUserDAO();
		}
		return instance;
	}
	
	public void saveLogin(String username, String token, String tokenSecret) {	

		user = username;

		try {

			String login = "INSERT INTO USER (USERNAME, ACCESS_TOKEN, ACCESS_SECRET) " +
					"VALUES (?,?,?);";

			PreparedStatement psmt = c.prepareStatement(login);

			psmt.setString(1, username);
			psmt.setString(2, token);
			psmt.setString(3, tokenSecret);
			psmt.executeUpdate();
			psmt.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Check if the user exists in the database to do the fast login
	 * @param user
	 * @return
	 */
	public boolean checkUser(String username) {
		
		ResultSet rs = null;
		
		try {
			
			String s = "SELECT username FROM user WHERE username=\""+username+"\" ";
			rs = c.createStatement().executeQuery(s);
			
			if(rs!=null) {
				return true;
			} else {
				return false;
			}

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		} finally{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get user data
	 * @param query
	 * @param user
	 * @return
	 */
	public String getUserData(String query, String username) {

		user = username;
		
		ResultSet rsu = null;

		try {

			String select = "SELECT "+query+" FROM user WHERE username=\""+user+"\" ";
			
			rsu = c.prepareStatement(select).executeQuery();
			
			return rsu.getString(query);

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
	}

}
