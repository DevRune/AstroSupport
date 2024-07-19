package nl.infinityastro.astrosupport.commands;

import nl.infinityastro.astrosupport.database.DatabaseManager;
import nl.infinityastro.astrosupport.utils.MessageUtils;
import nl.infinityastro.astrosupport.utils.MenuUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AskCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /ask <question>"));
            return true;
        }

        String question = String.join(" ", args);
        MenuUtils.submitAsk(player, question);
        player.sendMessage(MessageUtils.colorize("&aYour question has been submitted."));
        return true;
    }
}