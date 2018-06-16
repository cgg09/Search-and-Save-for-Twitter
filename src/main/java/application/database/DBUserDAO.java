package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import application.Main;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;

/**
 * @author María Cristina, cgg09
 *
 */
public class DBUserDAO {

	public static DBUserDAO instance;
	private String user;
	private Connection c;

	// Queries
	private String login = "INSERT INTO user (username, access_token, access_secret) VALUES (?,?,?)";
	private String countUsers = "SELECT count() FROM user";
	private String getUsers = "SELECT * FROM user";
	private String colections = "SELECT * FROM collection WHERE username= ? ORDER BY time_start DESC";
	private String delUser = "DELETE FROM user WHERE username = ?";

	private DBUserDAO() {
		c = Main.getDatabaseDAO().getConnection();
	}

	public static DBUserDAO getInstance() {
		if (instance == null) {
			instance = new DBUserDAO();
		}
		return instance;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Saves user information in the database
	 * @param username
	 * @param token
	 * @param tokenSecret
	 * @throws DatabaseWriteException
	 */
	public void saveLogin(String username, String token, String tokenSecret) throws DatabaseWriteException {

		setUser(username);

		PreparedStatement psmt;
		try {
			psmt = c.prepareStatement(login);
			psmt.setString(1, username);
			psmt.setString(2, token);
			psmt.setString(3, tokenSecret);
			psmt.executeUpdate();
			psmt.close();
		} catch (SQLException e) {
			throw new DatabaseWriteException("An error occurred while saving the data.",e);
		}

	}

	public List<String> getUsers() throws DatabaseReadException, DataNotFoundException {

		List<String> u = new Vector<String>();

		ResultSet rsu = null;

		try {
			if (!(c.createStatement().executeQuery(countUsers).getInt(1) < 1)) {
				rsu = c.createStatement().executeQuery(getUsers);
				while (rsu.next()) {
					u.add(rsu.getString("username"));
				}
			}

		} catch (SQLException e) {
			throw new DatabaseReadException("An error occurred while reading the data.",e);
		}
		return u;
	}
	
	/**
	 * Get user data
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws DatabaseReadException 
	 */
	public String getUserData(String data, String username) throws DatabaseReadException {

		setUser(username);
		String getUserData = "SELECT "+data+" FROM user WHERE username= ?";

		PreparedStatement psu = null;
		ResultSet rsu = null;

		try {
			psu = c.prepareStatement(getUserData);
			psu.setString(1, username);
			rsu = psu.executeQuery();
			return rsu.getString(data);
		} catch (SQLException e) {
			throw new DatabaseReadException("An error occurred while reading the data.",e);
		}

	}
	
	/**
	 * 
	 * @return
	 * @throws DatabaseReadException
	 */
	public List<DBCollection> retrieveCollections() throws DatabaseReadException {
		List<DBCollection> cols = new Vector<DBCollection>();

		PreparedStatement pscls = null;
		
		ResultSet rsc = null;
		

		try {
			pscls = c.prepareStatement(colections);
			pscls.setString(1, user);
			rsc = pscls.executeQuery();
			while (rsc.next()) {
				DBCollection dbc = new DBCollection(rsc.getString("type"));
				dbc.setId(rsc.getInt("collection_id"));
				dbc.setStart(rsc.getString("time_start"));
				//dbc.setEnd(rsc.getString("time_end"));
				dbc.setQuery(rsc.getString("query"));
				dbc.retrieveTweets();
				cols.add(dbc);
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("An error occurred while reading the data.",e);
		}

		return cols;
	}
	
	public void deleteUser() throws DatabaseWriteException {
		PreparedStatement psdt = null;
		try {
			psdt = c.prepareStatement(delUser);
			psdt.setString(1, user);
			psdt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseWriteException("There was an error deleting the user.", e);
		}
	}

}
