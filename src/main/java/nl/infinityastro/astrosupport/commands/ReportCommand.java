package nl.infinityastro.astrosupport.commands;

import nl.infinityastro.astrosupport.database.DatabaseManager;
import nl.infinityastro.astrosupport.utils.MessageUtils;
import nl.infinityastro.astrosupport.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cOnly players can use this command."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /report <player> <reason>"));
            return true;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);

        // Build the reason string from args[1] to args[args.length - 1]
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                reasonBuilder.append(" ");
            }
            reasonBuilder.append(args[i]);
        }
        String reason = reasonBuilder.toString();

        // Call the method to submit the report
        MenuUtils.submitReport((Player) sender, targetPlayer.getUniqueId(), reason);
        sender.sendMessage(MessageUtils.colorize("&aYour report has been submitted."));
        return true;
    }
}
