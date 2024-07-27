package nl.infinityastro.astrosupport.listeners;

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

            switch (plainTitle) {
                case "Type Menu":
                    handleTypeMenu(player, plainItemName);
                    break;
                case "Server Menu":
                    handleServerMenu(player, plainItemName);
                    break;
                case "Ask Menu":
                case "Report Menu":
                    handleAskReportMenu(player, plainTitle, plainItemName);
                    break;
                case "Ask Details":
                case "Report Details":
                    handleAskReportDetailsMenu(player, plainTitle, plainItemName);
                    break;
                default:
                    System.out.println("Unhandled menu title: " + plainTitle);
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
            if (itemName.contains("Claim")) {
                MenuUtils.claimItem(player, title.equalsIgnoreCase("Ask Menu") ? "ask" : "report", id);
            } else if (itemName.contains("Close")) {
                MenuUtils.closeItem(player, title.equalsIgnoreCase("Ask Menu") ? "ask" : "report", id);
            } else if (itemName.contains("Back")) {
                String type = playerTypeMap.get(player.getUniqueId());
                MenuUtils.openServerMenu(player, type);
            } else {
                MenuUtils.openItemDetails(player, title.equalsIgnoreCase("Ask Menu") ? "ask" : "report", id);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleAskReportDetailsMenu(Player player, String title, String itemName) {
        try {
            int id = Integer.parseInt(itemName.split(" ")[2]);
            if (itemName.contains("Claim")) {
                MenuUtils.claimItem(player, title.equalsIgnoreCase("Ask Details") ? "ask" : "report", id);
            } else if (itemName.contains("Close")) {
                MenuUtils.closeItem(player, title.equalsIgnoreCase("Ask Details") ? "ask" : "report", id);
            } else if (itemName.contains("Back")) {
                String previousMenu = title.equalsIgnoreCase("Ask Details") ? "Ask Menu" : "Report Menu";
                MenuUtils.openItemMenu(player, previousMenu.equals("Ask Menu") ? "ask" : "report", "server"); // Adjust server parameter as needed
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
