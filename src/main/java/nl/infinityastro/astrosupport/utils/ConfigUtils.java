package nl.infinityastro.astrosupport.utils;

import nl.infinityastro.astrosupport.AsteroSupport;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigUtils {

    private static FileConfiguration config;

    public static void initialize(AsteroSupport plugin) {
        config = plugin.getConfig();
    }

    public static List<String> getServerNames() {
        return config.getStringList("server.menu.items");
    }
}
