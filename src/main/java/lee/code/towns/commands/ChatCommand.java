package lee.code.towns.commands;

import lee.code.towns.Towns;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class ChatCommand implements CommandExecutor, TabCompleter {
  private final Towns towns;

  public ChatCommand(Towns towns) {
    this.towns = towns;
  }

  @Override
  public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
    towns.getCommandManager().performAsync(sender, towns.getCommandManager().getSubCommand("chat"), args);
    return true;
  }

  @Override
  public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
    return towns.getCommandManager().getSubCommand("chat").onTabComplete(sender, args);
  }
}
