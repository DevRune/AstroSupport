package nl.infinityastro.astrosupport.database;

import nl.infinityastro.astrosupport.AsteroSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Report {

    public static void submitReport(String reporter, String targetPlayer, String reason) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO reports (reporter, target_player, reason, status, server) VALUES (?, ?, ?, 'open', ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, reporter);
            stmt.setString(2, targetPlayer);
            stmt.setString(3, reason);
            stmt.setString(4, AsteroSupport.getPlugin(AsteroSupport.class).getConfig().getString("server.name"));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void claimReport(int id, String staffName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE reports SET claimed_by = ?, status = 'claimed' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, staffName);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeReport(int id) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE reports SET status = 'closed' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
