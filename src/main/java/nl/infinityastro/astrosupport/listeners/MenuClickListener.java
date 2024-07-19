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

public class MenuClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CHEST) {
            String title = MessageUtils.colorize("&6" + event.getView().getTitle().split(" ")[0]);
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                ItemMeta meta = clickedItem.getItemMeta();
                String itemName = meta.getDisplayName();

                if (title.equalsIgnoreCase(MessageUtils.colorize("&6Server Menu"))) {
                    if (itemName.equalsIgnoreCase(MessageUtils.colorize("&cBack"))) {
                        // Handle back button (return to previous menu or close menu)
                        return;
                    }

                    // Open Ask or Report menu for the selected server
                    MenuUtils.openTypeMenu((Player) event.getWhoClicked());
                } else if (title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Menu")) ||
                        title.equalsIgnoreCase(MessageUtils.colorize("&6Report Menu"))) {
                    try {
                        int id = Integer.parseInt(itemName.split(" ")[2]);

                        if (itemName.contains(MessageUtils.colorize("&aClaim"))) {
                            MenuUtils.claimItem((Player) event.getWhoClicked(), title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Menu")) ? "ask" : "report", id);
                        } else if (itemName.contains(MessageUtils.colorize("&cClose"))) {
                            MenuUtils.closeItem((Player) event.getWhoClicked(), title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Menu")) ? "ask" : "report", id);
                        } else if (itemName.contains(MessageUtils.colorize("&cBack"))) {
                            MenuUtils.openTypeMenu((Player) event.getWhoClicked());
                        } else {
                            MenuUtils.openItemDetails((Player) event.getWhoClicked(), title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Menu")) ? "ask" : "report", id);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Details")) ||
                        title.equalsIgnoreCase(MessageUtils.colorize("&6Report Details"))) {
                    try {
                        int id = Integer.parseInt(itemName.split(" ")[2]);

                        if (itemName.contains(MessageUtils.colorize("&aClaim"))) {
                            MenuUtils.claimItem((Player) event.getWhoClicked(), title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Details")) ? "ask" : "report", id);
                        } else if (itemName.contains(MessageUtils.colorize("&cClose"))) {
                            MenuUtils.closeItem((Player) event.getWhoClicked(), title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Details")) ? "ask" : "report", id);
                        } else if (itemName.contains(MessageUtils.colorize("&cBack"))) {
                            String server = event.getView().getTitle().split(" ")[1].toLowerCase();
                            MenuUtils.openItemMenu((Player) event.getWhoClicked(), title.equalsIgnoreCase(MessageUtils.colorize("&6Ask Details")) ? "ask" : "report", server);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
