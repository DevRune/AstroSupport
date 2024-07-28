package nl.infinityastro.astrosupport.database;

import nl.infinityastro.astrosupport.AsteroSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Report {

    static AsteroSupport plugin = AsteroSupport.getPlugin(AsteroSupport.class);

    public static void submitReport(UUID reporter, UUID targetPlayer, String reason) {
        try (Connection conn = plugin.getDatabaseManager().getConnection()) {
            String query = "INSERT INTO reports (reporter, target_player, reason, status, server) VALUES (?, ?, ?, 'open', ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, reporter.toString());
            stmt.setString(2, targetPlayer.toString());
            stmt.setString(3, reason);
            stmt.setString(4, AsteroSupport.getPlugin(AsteroSupport.class).getConfig().getString("server.name"));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void claimReport(int id, UUID staff) {
        try (Connection conn = plugin.getDatabaseManager().getConnection()) {
            String query = "UPDATE reports SET claimed_by = ?, status = 'claimed' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, staff.toString());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unclaimReport(int id) {
        try (Connection conn = plugin.getDatabaseManager().getConnection()) {
            String query = "UPDATE reports SET claimed_by = NULL, status = 'open' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeReport(int id) {
        try (Connection conn = plugin.getDatabaseManager().getConnection()) {
            String query = "UPDATE reports SET status = 'closed' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
