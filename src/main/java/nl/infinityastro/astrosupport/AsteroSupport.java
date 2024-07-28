package nl.infinityastro.astrosupport;

import nl.infinityastro.astrosupport.commands.AskCommand;
import nl.infinityastro.astrosupport.commands.ReportCommand;
import nl.infinityastro.astrosupport.commands.SupportCommand;
import nl.infinityastro.astrosupport.database.DatabaseManager;
import nl.infinityastro.astrosupport.listeners.MenuClickListener;
import nl.infinityastro.astrosupport.utils.MenuUtils;
import nl.infinityastro.astrosupport.utils.StaffNotificationTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AsteroSupport extends JavaPlugin {

    private List<String> serverNames;
    private DatabaseManager databaseManager;
    private StaffNotificationTask staffNotificationTask;

    @Override
    public void onEnable() {
        // Load configuration and set up the database connection
        databaseManager = new DatabaseManager();
        saveDefaultConfig();
        String url = getConfig().getString("database.url");
        String user = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");
        String type = getConfig().getString("database.type");
        databaseManager.configureDatabase(url, user, password, type);
        initializeDatabase();

        // Set database manager for utility classes
        MenuUtils.setDatabaseManager(databaseManager);

        //start staffNotificationTask
        staffNotificationTask = new StaffNotificationTask(databaseManager);
        staffNotificationTask.runTaskTimer(this, 20L * 60 * 5, 20L * 60 * 5);

        // Load server names from the configuration
        serverNames = getConfig().getStringList("server.menu.items");
        MenuUtils.setServerNames(serverNames);

        // Register commands and event listeners
        getServer().getPluginManager().registerEvents(new MenuClickListener(), this);
        getCommand("ask").setExecutor(new AskCommand());
        getCommand("report").setExecutor(new ReportCommand());
        getCommand("support").setExecutor(new SupportCommand());
    }

    public void initializeDatabase() {
        String createAskTable = "CREATE TABLE IF NOT EXISTS asks (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "player_name VARCHAR(64), " +
                "question TEXT, " +
                "server VARCHAR(255), " +
                "status VARCHAR(50) DEFAULT 'open', " +
                "claimed_by VARCHAR(36), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String createReportTable = "CREATE TABLE IF NOT EXISTS reports (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "reporter VARCHAR(36), " +
                "target_player VARCHAR(36), " +
                "reason TEXT, " +
                "server VARCHAR(255), " +
                "status VARCHAR(50) DEFAULT 'open', " +
                "claimed_by VARCHAR(36), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createAskTable);
            stmt.execute(createReportTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Clean up resources if needed
        staffNotificationTask.cancel();
        databaseManager.disconnect();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
