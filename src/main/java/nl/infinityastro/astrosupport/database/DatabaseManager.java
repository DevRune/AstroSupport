package nl.infinityastro.astrosupport.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static String url;
    private static String user;
    private static String password;
    private static String dbType;
    private static Connection connection;

    // Initialize database configuration
    public static void configureDatabase(String url, String user, String password, String dbType) {
        DatabaseManager.url = url;
        DatabaseManager.user = user;
        DatabaseManager.password = password;
        DatabaseManager.dbType = dbType.toLowerCase();
        loadDriver();
    }

    // Load the appropriate JDBC driver based on the database type
    private static void loadDriver() {
        try {
            if ("mysql".equals(dbType)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else if ("mariadb".equals(dbType)) {
                Class.forName("org.mariadb.jdbc.Driver");
            } else {
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("JDBC Driver not found.", e);
        }
    }

    // Get the database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                if (url == null || user == null || password == null) {
                    throw new IllegalStateException("Database configuration is not set.");
                }
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Failed to connect to the database.");
            }
        }
        return connection;
    }

    // Close the database connection
    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
