package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Connector class with static methods for connecting to an SQLite database.
 */
public final class SQLConnector {
	
	//Database file location
	public static String url;
	
	/**
	 * Set database file location.
	 * @param url Database file location
	 */
	public static void setUrl(String url) {
		SQLConnector.url = url;
	}

	/**
	 * Check for SQLite database tables and create them if not found.
	 * @param tableStrings ArrayList of SQL command strings, one for each table, formatted as "CREATE TABLE IF NOT EXISTS [tableName] ([columns])"
	 */
	public static void checkTables(ArrayList<String> tableStrings) {
		try(
				Connection connection = DriverManager.getConnection(SQLConnector.url);
				Statement statement = connection.createStatement();
				) {

			for (String tableString : tableStrings) {
				statement.execute(tableString);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/**
	 * Insert data into SQLite database with protection against injection.
	 * @param insertString SQL command string, formatted as "INSERT INTO [tableName] ([columns) VALUES ([?, times columnCount])";
	 * @param data Array of Strings of data to insert, with the number of Strings matching the number of columns
	 * @return Auto-incremented ID of the inserted row in the table
	 */
	public static int insert (String insertString, String[] data) {
		int ID = 0;

		try(
				Connection connection = DriverManager.getConnection(SQLConnector.url);
				PreparedStatement statement = connection.prepareStatement(insertString, Statement.RETURN_GENERATED_KEYS);
				) {
			for (int i = 0; i < data.length; i++) {
				statement.setString(i + 1, data[i]);
			}
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				ID = resultSet.getInt(1);
			}
			resultSet.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		return ID;
	}

	/**
	 * Read data from SQLite database.
	 * @param readString SQL command string, formatted as "SELECT * FROM [tableName] WHERE [columnName] = [value]"
	 * @param processor Consumer object for holding the ResultSet from the query after the SQL connection closes
	 */
	public static void read(String readString, Consumer<ResultSet> processor) {
		try(
				Connection connection = DriverManager.getConnection(SQLConnector.url);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(readString);
				) {
			processor.accept(resultSet);
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/**
	 * Update data in SQLite database.
	 * @param updateString SQL command string, formatted as "UPDATE [tableName] SET [columnName] = ?, WHERE [columnName] = [value]"
	 * @param data Array of Strings of data to update, with the number of Strings matching the number of columns
	 */
	public static void update(String updateString, String[] data) {
		try(
				Connection connection = DriverManager.getConnection(SQLConnector.url);
				PreparedStatement statement = connection.prepareStatement(updateString);
			) {
			for (int i = 0; i < data.length; i++) {
				statement.setString(i + 1, data[i]);
			}
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/**
	 * Delete data in SQLite database.
	 * @param deleteString SQL command string, formatted as "DELETE FROM [tableName] WHERE [columnName] = [value]"
	 */
	public static void delete(String deleteString) {
		try(
				Connection connection = DriverManager.getConnection(SQLConnector.url);
				Statement statement = connection.createStatement();
			) {
			statement.executeUpdate(deleteString);
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
