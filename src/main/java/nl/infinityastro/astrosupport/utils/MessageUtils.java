package nl.infinityastro.astrosupport.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String removeColorCodes(String input) {
        return input.replaceAll("(?i)\\u00A7[0-9a-fk-or]", "");
    }
}
