package lee.code.towns.commands;

import lee.code.towns.Towns;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatCommand implements CommandExecutor, TabCompleter {

    private final Towns towns;

    public ChatCommand(Towns towns) {
        this.towns = towns;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (sender instanceof Player player) towns.getCommandManager().getSubCommands().get(0).perform(player, args);
        else towns.getCommandManager().getSubCommands().get(0).performConsole(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        return towns.getCommandManager().getSubCommands().get(0).onTabComplete(sender, args);
    }
}
