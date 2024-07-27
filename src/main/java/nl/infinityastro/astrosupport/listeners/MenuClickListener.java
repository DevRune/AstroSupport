package nl.infinityastro.astrosupport.listeners;

import nl.infinityastro.astrosupport.database.Ask;
import nl.infinityastro.astrosupport.database.Report;
import nl.infinityastro.astrosupport.utils.MenuUtils;
import nl.infinityastro.astrosupport.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MenuClickListener implements Listener {

    HashMap<UUID, String> playerTypeMap = new HashMap<>();
    private HashMap<UUID, Integer> playerIdMap = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CHEST) {
            event.setCancelled(true); // Prevent item moving

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return; // Ignore if no item meta
            }

            ItemMeta meta = clickedItem.getItemMeta();
            String itemName = meta.getDisplayName();
            Player player = (Player) event.getWhoClicked();

            String plainTitle = MessageUtils.removeColorCodes(event.getView().getTitle());
            String plainItemName = MessageUtils.removeColorCodes(itemName);

            switch (plainTitle.toLowerCase()) {
                case "type menu":
                    handleTypeMenu(player, plainItemName);
                    break;
                case "server menu":
                    handleServerMenu(player, plainItemName);
                    break;
                case "ask menu":
                case "report menu":
                    handleAskReportMenu(player, plainTitle, plainItemName);
                    break;
                case "ask details":
                case "report details":
                    handleAskReportDetailsMenu(player, plainTitle, plainItemName);
                    break;
            }
        }
    }

    private void handleTypeMenu(Player player, String itemName) {
        if (itemName.equalsIgnoreCase("Back")) {
            MenuUtils.openTypeMenu(player);
        } else {
            playerTypeMap.put(player.getUniqueId(), itemName.toLowerCase());
            MenuUtils.openServerMenu(player, itemName.toLowerCase());
        }
    }

    private void handleServerMenu(Player player, String itemName) {
        if (itemName.equalsIgnoreCase("Back")) {
            MenuUtils.openTypeMenu(player);
        } else {
            String type = playerTypeMap.get(player.getUniqueId());
            MenuUtils.openItemMenu(player, type, itemName);
        }
    }

    private void handleAskReportMenu(Player player, String title, String itemName) {
        try {
            int id = Integer.parseInt(itemName.split(" ")[2]);
            playerIdMap.put(player.getUniqueId(), id);
            MenuUtils.openItemDetails(player, title.equalsIgnoreCase("Ask Menu") ? "ask" : "report", id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleAskReportDetailsMenu(Player player, String title, String itemName) {
        try {
            int id = playerIdMap.get(player.getUniqueId());
            if (title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Details"))) {
                // Handle Ask details menu interactions
                switch (itemName) {
                    case "&aClaim":
                        player.sendMessage(MessageUtils.colorize("&aYou have claimed this Ask!"));
                        Ask.claimAsk(id, player.getUniqueId());
                        break;
                    case "&cClose":
                        player.sendMessage(MessageUtils.colorize("&cYou have closed this Ask!"));
                        Ask.closeAsk(id);
                        break;
                    case "&cBack":
                        MenuUtils.openServerMenu(player, "Ask");
                        break;
                    default:
                }
            } else if (title.equalsIgnoreCase(MessageUtils.colorize("&6Report Details"))) {
                // Handle Report details menu interactions
                switch (itemName) {
                    case "&aClaim":
                        player.sendMessage(MessageUtils.colorize("&aYou have claimed this Report!"));
                        Report.claimReport(id, player.getUniqueId());
                        break;
                    case "&cClose":
                        player.sendMessage(MessageUtils.colorize("&cYou have closed this Report!"));
                        Report.closeReport(id);
                        break;
                    case "&cBack":
                        MenuUtils.openServerMenu(player, "Report");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
