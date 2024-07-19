package nl.infinityastro.astrosupport.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static Connection connection;

    // Configure database connection
    public void configureDatabase(String url, String user, String password) {
        URL = url;
        USER = user;
        PASSWORD = password;
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Failed to connect to the database.");
            }
        }
        return connection;
    }
}
