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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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

        try (Connection conn = databaseManager.getConnection()) {
            String query = "SELECT * FROM " + type + " WHERE server = ? AND status = 'open'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, server);
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

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(MessageUtils.colorize("&cBack"));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(53, backButton);

        player.openInventory(inv);
    }

    public static void openItemDetails(Player player, String type, int id) {
        Inventory inv = Bukkit.createInventory(null, 9, MessageUtils.colorize("&6" + type + " Details"));

        try (Connection conn = databaseManager.getConnection()) {
            String query = "SELECT * FROM " + type + " WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ItemStack detailsItem = new ItemStack(Material.PAPER);
                ItemMeta detailsMeta = detailsItem.getItemMeta();
                if (detailsMeta != null) {
                    detailsMeta.setDisplayName(MessageUtils.colorize("&aDetails for " + type + " ID " + id));
                    detailsItem.setItemMeta(detailsMeta);
                    inv.addItem(detailsItem);
                }

                String description = rs.getString("description");
                ItemStack descriptionItem = new ItemStack(Material.WRITTEN_BOOK);
                ItemMeta descriptionMeta = descriptionItem.getItemMeta();
                if (descriptionMeta != null) {
                    descriptionMeta.setDisplayName(MessageUtils.colorize("&eDescription"));
                    descriptionMeta.setLore(Collections.singletonList(MessageUtils.colorize("&7" + description)));
                    descriptionItem.setItemMeta(descriptionMeta);
                    inv.addItem(descriptionItem);
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

    public static void submitReport(Player player, String targetPlayer, String reason) {
        Report.submitReport(player.getName(), targetPlayer, reason);
    }

    public static void claimItem(Player player, String type, int id) {
        if (type.equalsIgnoreCase("ask")) {
            Ask.claimAsk(id, player.getName());
        } else if (type.equalsIgnoreCase("report")) {
            Report.claimReport(id, player.getName());
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
