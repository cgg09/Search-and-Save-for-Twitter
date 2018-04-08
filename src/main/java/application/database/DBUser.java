package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUser {

	private String user;
	
	private Connection c;
	private String databasePath;

	public DBUser(String databasePath) {
		this.databasePath = databasePath;
	}

	
	public void connect() {
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	

	public void saveLogin(String username, String token, String tokenSecret) {	

		user = username;

		try {
			
			connect();
			
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
			
			connect(); 
			
			String s = "SELECT username FROM user WHERE username=\""+username+"\" ";
			
			
			
			rs = c.createStatement().executeQuery(s);
			
			//System.out.println("Result: "+rs.getString(user));
			
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
				// TODO Auto-generated catch block
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
			
			while (rsu.next()) {			
				return rsu.getString(query);
			}

			return null;

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
	}

}
