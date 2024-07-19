package nl.infinityastro.astrosupport;

import nl.infinityastro.astrosupport.commands.AskCommand;
import nl.infinityastro.astrosupport.commands.ReportCommand;
import nl.infinityastro.astrosupport.commands.ReportsCommand;
import nl.infinityastro.astrosupport.commands.SupportCommand;
import nl.infinityastro.astrosupport.database.DatabaseManager;
import nl.infinityastro.astrosupport.listeners.MenuClickListener;
import nl.infinityastro.astrosupport.utils.MenuUtils;
import nl.infinityastro.astrosupport.utils.ConfigUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;

public class AsteroSupport extends JavaPlugin {

    private List<String> serverNames;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        // Load configuration and set up the database connection
        databaseManager = new DatabaseManager();
        saveDefaultConfig();
        String url = getConfig().getString("database.url");
        String user = getConfig().getString("database.user");
        String password = getConfig().getString("database.password");

        // Configure database connection
        databaseManager.configureDatabase(url, user, password);

        // Set database manager for utility classes
        MenuUtils.setDatabaseManager(databaseManager);

        // Load server names from the configuration
        serverNames = getConfig().getStringList("server.menu.items");
        MenuUtils.setServerNames(serverNames);

        // Register commands and event listeners
        getServer().getPluginManager().registerEvents(new MenuClickListener(), this);
        getCommand("ask").setExecutor(new AskCommand());
        getCommand("report").setExecutor(new ReportCommand());
        getCommand("reports").setExecutor(new ReportsCommand());
        getCommand("support").setExecutor(new SupportCommand());
    }

    @Override
    public void onDisable() {
        // Clean up resources if needed
    }
}
