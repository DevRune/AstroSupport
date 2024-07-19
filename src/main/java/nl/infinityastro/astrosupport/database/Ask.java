package nl.infinityastro.astrosupport.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Ask {

    public static void submitAsk(String playerName, String question) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO asks (player_name, question, status, server) VALUES (?, ?, 'open', ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, playerName);
            stmt.setString(2, question);
            stmt.setString(3, "survival"); // Modify as needed
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void claimAsk(int id, String staffName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE asks SET claimed_by = ?, status = 'claimed' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, staffName);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeAsk(int id) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE asks SET status = 'closed' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
