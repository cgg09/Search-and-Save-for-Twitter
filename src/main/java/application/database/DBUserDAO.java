package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Vector;

import application.Main;
import application.exceptions.DataNotFoundException;
import application.exceptions.DatabaseReadException;
import application.exceptions.DatabaseWriteException;

/**
 * @author María Cristina
 *
 */
public class DBUserDAO {

	public static DBUserDAO instance;
	private String user;
	private Connection c;

	// Queries
	private String login = "INSERT INTO user (USERNAME, ACCESS_TOKEN, ACCESS_SECRET) " + "VALUES (?,?,?)";
	private String count = "SELECT count() FROM user";
	private String users = "SELECT * FROM user";

	private DBUserDAO() {
		c = Main.getDatabaseDAO().getConnection();
	}

	public static DBUserDAO getInstance() {
		if (instance == null) {
			instance = new DBUserDAO();
		}
		return instance;
	}

	public void saveLogin(String username, String token, String tokenSecret) throws DatabaseWriteException {

		user = username;

		PreparedStatement psmt;
		try {
			psmt = c.prepareStatement(login);
			psmt.setString(1, username);
			psmt.setString(2, token);
			psmt.setString(3, tokenSecret);
			psmt.executeUpdate();
			psmt.close();
		} catch (SQLException e) {
			throw new DatabaseWriteException("An error occurred while saving the data.");
		}

	}

	/**
	 * Check if the user exists in the database to do the fast login
	 * 
	 * @param user
	 * @return
	 * @throws DatabaseReadException
	 * @throws DataNotFoundException
	 */
	public boolean checkUser(String username) throws DatabaseReadException, DataNotFoundException {

		ResultSet rs = null;

		String s = "SELECT username FROM user WHERE username=\"" + username + "\" ";
		try {
			rs = c.createStatement().executeQuery(s);
		} catch (SQLException e) {
			throw new DatabaseReadException("An error occurred while reading the data.");
		}

		if (rs != null) {
			return true;
		} else {
			throw new DataNotFoundException("The user was not found in the database."); // Debería ser un error ¿?
		}

	}

	/**
	 * Get user data
	 * 
	 * @param query
	 * @param user
	 * @return
	 * @throws DatabaseReadException 
	 */
	public String getUserData(String query, String username) throws DatabaseReadException {

		user = username;

		ResultSet rsu;

		String select = "SELECT " + query + " FROM user WHERE username=\"" + user + "\" ";

		try {
			rsu = c.prepareStatement(select).executeQuery();
			return rsu.getString(query);
		} catch (SQLException e) {
			throw new DatabaseReadException("An error occurred while reading the data.");
		}

	}

	public List<String> getUsers() throws DatabaseReadException, DataNotFoundException {

		List<String> u = new Vector<String>();

		ResultSet rsu = null;

		try {
			if (c.createStatement().executeQuery(count).getInt(1) < 1) { // FIXME no es una "EXCEPTION", qué poner aquí ?
				return null; // FIXME throw new DataNotFoundException();
			}
			rsu = c.createStatement().executeQuery(users);
			while (rsu.next()) {
				u.add(rsu.getString("username"));
			}

		} catch (SQLException e) {
			throw new DatabaseReadException("An error occurred while reading the data.");
		}

		return u;
	}
	
	/**
	 * 
	 * @return
	 * @throws DatabaseReadException
	 */
	public List<DBCollection> retrieveCollections() throws DatabaseReadException {
		List<DBCollection> cols = new Vector<DBCollection>();

		ResultSet rsc = null;
		String col = "SELECT * FROM collection WHERE username=\"" + user + "\"";

		try {
			rsc = c.createStatement().executeQuery(col);
			while (rsc.next()) {
				DBCollection dbc = new DBCollection(rsc.getString("type"));
				dbc.setId(rsc.getInt("collection_id"));
				dbc.setStart(LocalDateTime.parse(rsc.getString("time_start")));
				dbc.setEnd(LocalDateTime.parse(rsc.getString("time_start")));
				dbc.setQuery(rsc.getString("query"));
				dbc.updateTweets();
				cols.add(dbc);
			}
		} catch (SQLException e) {
			throw new DatabaseReadException("An error occurred while reading the data.");
		}

		return cols;
	}

}
