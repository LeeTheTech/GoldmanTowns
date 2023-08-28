package lee.code.towns.commands;

import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
  private final CommandManager commandManager;

  public TabCompletion(CommandManager commandManager) {
    this.commandManager = commandManager;
  }

  @Override
  public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
    if (commandManager.getSubCommands().containsKey(args[0].toLowerCase())) {
      return commandManager.getSubCommand(args[0].toLowerCase()).onTabComplete(sender, args);
    } else return new ArrayList<>();
  }
}
