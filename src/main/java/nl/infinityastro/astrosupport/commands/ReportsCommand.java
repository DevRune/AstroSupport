package nl.infinityastro.astrosupport.commands;

import nl.infinityastro.astrosupport.utils.MessageUtils;
import nl.infinityastro.astrosupport.utils.MenuUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cOnly players can use this command."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /reports <list/view> [ID]"));
            return true;
        }

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("list")) {
            MenuUtils.openReportsMenu(player);
        } else if (args[0].equalsIgnoreCase("view")) {
            if (args.length < 2) {
                player.sendMessage(MessageUtils.colorize("&cUsage: /reports view <ID>"));
                return true;
            }

            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(MessageUtils.colorize("&cInvalid report ID."));
                return true;
            }

            MenuUtils.openItemDetails(player, "report", id);
        } else {
            player.sendMessage(MessageUtils.colorize("&cUnknown subcommand."));
        }
        return true;
    }
}
