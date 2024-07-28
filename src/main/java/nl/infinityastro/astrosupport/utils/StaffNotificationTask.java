package nl.infinityastro.astrosupport.utils;

import nl.infinityastro.astrosupport.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffNotificationTask extends BukkitRunnable {

    private final DatabaseManager databaseManager;

    public StaffNotificationTask(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void run() {
        int openAsks = 0;
        int openReports = 0;
        int claimedAsks = 0;
        int claimedReports = 0;

        try (Connection conn = databaseManager.getConnection()) {
            // Query to count open asks
            String openAsksQuery = "SELECT COUNT(*) FROM asks WHERE status = 'open'";
            try (PreparedStatement stmt = conn.prepareStatement(openAsksQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    openAsks = rs.getInt(1);
                }
            }

            // Query to count open reports
            String openReportsQuery = "SELECT COUNT(*) FROM reports WHERE status = 'open'";
            try (PreparedStatement stmt = conn.prepareStatement(openReportsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    openReports = rs.getInt(1);
                }
            }

            // Query to count claimed asks by each staff member
            String claimedAsksQuery = "SELECT COUNT(*) FROM asks WHERE status = 'claimed' AND claimed_by = ?";
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("staff.permission")) { // Replace with your actual permission check
                    try (PreparedStatement stmt = conn.prepareStatement(claimedAsksQuery)) {
                        stmt.setString(1, player.getUniqueId().toString());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                claimedAsks = rs.getInt(1);
                            }
                        }
                    }

                    // Query to count claimed reports by each staff member
                    String claimedReportsQuery = "SELECT COUNT(*) FROM reports WHERE status = 'claimed' AND claimed_by = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(claimedReportsQuery)) {
                        stmt.setString(1, player.getUniqueId().toString());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                claimedReports = rs.getInt(1);
                            }
                        }
                    }

                    // Send message to staff member
                    if(openAsks != 0 || openReports != 0) {
                        player.sendMessage(MessageUtils.colorize("&6There are currently &c" + openAsks + " &6open questions and &c" + openReports + " &6open reports."));
                    }
                    if(claimedAsks != 0 || claimedReports != 0) {
                        player.sendMessage(MessageUtils.colorize("&6You have claimed &c" + claimedAsks + " &6questions and &c" + claimedReports + " &6reports."));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

