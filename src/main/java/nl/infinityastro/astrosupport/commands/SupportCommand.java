package nl.infinityastro.astrosupport.commands;

import nl.infinityastro.astrosupport.utils.MessageUtils;
import nl.infinityastro.astrosupport.utils.MenuUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SupportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cOnly players can use this command."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /support"));
            return true;
        }

        Player player = (Player) sender;
        MenuUtils.openTypeMenu(player);
        return true;
    }
}
