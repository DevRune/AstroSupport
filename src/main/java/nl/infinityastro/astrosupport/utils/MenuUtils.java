package nl.infinityastro.astrosupport.utils;

import nl.infinityastro.astrosupport.database.Ask;
import nl.infinityastro.astrosupport.database.DatabaseManager;
import nl.infinityastro.astrosupport.database.Report;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MenuUtils {

    private static DatabaseManager databaseManager;
    private static List<String> serverNames;

    public static void setDatabaseManager(DatabaseManager databaseManager) {
        MenuUtils.databaseManager = databaseManager;
    }

    public static void setServerNames(List<String> serverNames) {
        MenuUtils.serverNames = serverNames;
    }

    public static void openTypeMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.colorize("&6Type Menu"));

        String[] types = {"Ask", "Report"};

        for (int i = 0; i < types.length; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(MessageUtils.colorize("&6" + types[i]));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(MessageUtils.colorize("&cBack"));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(26, backButton);

        player.openInventory(inv);
    }

    public static void openServerMenu(Player player, String type) {
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.colorize("&6Server Menu"));

        for (int i = 0; i < serverNames.size(); i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(MessageUtils.colorize("&6" + serverNames.get(i)));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(MessageUtils.colorize("&cBack"));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(26, backButton);

        player.openInventory(inv);
    }

    public static void openItemMenu(Player player, String type, String server) {
        Inventory inv = Bukkit.createInventory(null, 54, MessageUtils.colorize("&6" + type + " Menu"));

        String playerUUID = player.getUniqueId().toString(); // Verkrijg de UUID van de huidige speler

        try (Connection conn = databaseManager.getConnection()) {
            // SQL-query om zowel open als geclaimde items op te halen die door de speler zijn geclaimd
            String query = "SELECT * FROM " + getDatabaseFromType(type) +
                    " WHERE server = ? AND (status = 'open' OR (status = 'claimed' AND claimed_by = ?))";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, server);
            stmt.setString(2, playerUUID); // Stel de UUID in voor geclaimde items
            ResultSet rs = stmt.executeQuery();

            int index = 0;
            while (rs.next() && index < 54) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(MessageUtils.colorize("&6" + type.substring(0, 1).toUpperCase() + type.substring(1) + " ID " + rs.getInt("id")));
                    item.setItemMeta(meta);
                    inv.setItem(index++, item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Voeg de 'Back' knop toe
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(MessageUtils.colorize("&cBack"));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(53, backButton);

        player.openInventory(inv);
    }


    private static String getDatabaseFromType(String type) {
        return type.toLowerCase() + "s";
    }

    public static void openItemDetails(Player player, String type, int id) {
        Inventory inv = Bukkit.createInventory(null, 3*9, MessageUtils.colorize("&6" + type + " Details (" + id + ")"));

        try (Connection conn = databaseManager.getConnection()) {
            String query = "SELECT * FROM " + getDatabaseFromType(type) + " WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                // Define the 'Claim' and 'Close' buttons
                ItemStack claimButton = new ItemStack(Material.GREEN_CONCRETE);
                ItemMeta claimMeta = claimButton.getItemMeta();
                if (claimMeta != null) {
                    claimMeta.setDisplayName(MessageUtils.colorize("&aClaim"));
                    claimButton.setItemMeta(claimMeta);
                }

                ItemStack closeButton = new ItemStack(Material.RED_CONCRETE);
                ItemMeta closeMeta = closeButton.getItemMeta();
                if (closeMeta != null) {
                    closeMeta.setDisplayName(MessageUtils.colorize("&cClose"));
                    closeButton.setItemMeta(closeMeta);
                }

// Add Claim and Close buttons to the menu
                inv.setItem(24, claimButton); // Slot 24 for Claim button
                inv.setItem(26, closeButton); // Slot 26 for Close button

                if(type.equalsIgnoreCase("ask")) {
                    // Add "Ask" type items to the menu
                    String playerName = rs.getString("player_name");
                    String question = rs.getString("question");
                    String server = rs.getString("server");
                    String status = rs.getString("status");
                    String claimedBy = rs.getString("claimed_by");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    // Example: Add player name item
                    ItemStack playerNameItem = new ItemStack(Material.NAME_TAG);
                    ItemMeta playerNameMeta = playerNameItem.getItemMeta();
                    if (playerNameMeta != null) {
                        playerNameMeta.setDisplayName(MessageUtils.colorize("&ePlayer Name"));
                        playerNameMeta.setLore(Collections.singletonList(MessageUtils.colorize("&7" + playerName)));
                        playerNameItem.setItemMeta(playerNameMeta);
                    }
                    inv.setItem(10, playerNameItem); // Slot 10 for player name

                    // Add more items for question, server, etc. as needed
                    // Example for question
                    ItemStack questionItem = new ItemStack(Material.BOOK);
                    ItemMeta questionMeta = questionItem.getItemMeta();
                    if (questionMeta != null) {
                        questionMeta.setDisplayName(MessageUtils.colorize("&eQuestion"));
                        questionMeta.setLore(Collections.singletonList(MessageUtils.colorize("&7" + question)));
                        questionItem.setItemMeta(questionMeta);
                    }
                    inv.setItem(12, questionItem); // Slot 12 for question

                    // Example for server
                    ItemStack serverItem = new ItemStack(Material.COMPASS);
                    ItemMeta serverMeta = serverItem.getItemMeta();
                    if (serverMeta != null) {
                        serverMeta.setDisplayName(MessageUtils.colorize("&eServer"));
                        serverMeta.setLore(Collections.singletonList(MessageUtils.colorize("&7" + server)));
                        serverItem.setItemMeta(serverMeta);
                    }
                    inv.setItem(14, serverItem); // Slot 14 for server
                }

                if(type.equalsIgnoreCase("report")) {
                    // Add "Report" type items to the menu
                    String reporter = rs.getString("reporter");
                    String targetPlayer = rs.getString("target_player");
                    String reason = rs.getString("reason");
                    String server = rs.getString("server");
                    String status = rs.getString("status");
                    String claimedBy = rs.getString("claimed_by");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    // Example: Add reason item
                    ItemStack reasonItem = new ItemStack(Material.WRITTEN_BOOK);
                    ItemMeta reasonMeta = reasonItem.getItemMeta();
                    if (reasonMeta != null) {
                        reasonMeta.setDisplayName(MessageUtils.colorize("&eReason"));
                        reasonMeta.setLore(Collections.singletonList(MessageUtils.colorize("&7" + reason)));
                        reasonItem.setItemMeta(reasonMeta);
                    }
                    inv.setItem(10, reasonItem); // Slot 10 for reason

                    // Add more items for reporter, target player, server, etc. as needed
                    // Example for reporter
                    ItemStack reporterItem = new ItemStack(Material.PAPER);
                    ItemMeta reporterMeta = reporterItem.getItemMeta();
                    if (reporterMeta != null) {
                        reporterMeta.setDisplayName(MessageUtils.colorize("&eReporter"));
                        reporterMeta.setLore(Collections.singletonList(MessageUtils.colorize("&7" + reporter)));
                        reporterItem.setItemMeta(reporterMeta);
                    }
                    inv.setItem(12, reporterItem); // Slot 12 for reporter

                    // Example for target player
                    ItemStack targetPlayerItem = new ItemStack(Material.NAME_TAG);
                    ItemMeta targetPlayerMeta = targetPlayerItem.getItemMeta();
                    if (targetPlayerMeta != null) {
                        targetPlayerMeta.setDisplayName(MessageUtils.colorize("&eTarget Player"));
                        targetPlayerMeta.setLore(Collections.singletonList(MessageUtils.colorize("&7" + targetPlayer)));
                        targetPlayerItem.setItemMeta(targetPlayerMeta);
                    }
                    inv.setItem(14, targetPlayerItem); // Slot 14 for target player
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ItemStack claimButton = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta claimMeta = claimButton.getItemMeta();
        if (claimMeta != null) {
            claimMeta.setDisplayName(MessageUtils.colorize("&aClaim"));
            claimButton.setItemMeta(claimMeta);
        }
        inv.addItem(claimButton);

        ItemStack closeButton = new ItemStack(Material.RED_CONCRETE);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(MessageUtils.colorize("&cClose"));
            closeButton.setItemMeta(closeMeta);
        }
        inv.addItem(closeButton);

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(MessageUtils.colorize("&cBack"));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(8, backButton);

        player.openInventory(inv);
    }

    public static void submitAsk(Player player, String question) {
        Ask.submitAsk(player.getName(), question);
    }

    public static void submitReport(Player player, UUID targetPlayer, String reason) {
        Report.submitReport(player.getUniqueId(), targetPlayer, reason);
    }

    public static void claimItem(Player player, String type, int id) {
        if (type.equalsIgnoreCase("ask")) {
            Ask.claimAsk(id, player.getUniqueId());
        } else if (type.equalsIgnoreCase("report")) {
            Report.claimReport(id, player.getUniqueId());
        }
    }

    public static void closeItem(Player player, String type, int id) {
        if (type.equalsIgnoreCase("ask")) {
            Ask.closeAsk(id);
            player.sendMessage("§aSuccesfully closed this question.");
        } else if (type.equalsIgnoreCase("report")) {
            Report.closeReport(id);
            player.sendMessage("§aSuccesfully closed this report.");
        }
    }
}
