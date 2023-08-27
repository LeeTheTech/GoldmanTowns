package lee.code.towns.commands;

import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabCompletion implements TabCompleter {
  private final CommandManager commandManager;

  public TabCompletion(CommandManager commandManager) {
    this.commandManager = commandManager;
  }

  @Override
  public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
    final Optional<SubCommand> matchingSubCommand = commandManager.getSubCommands()
      .stream()
      .filter(subCommand -> args[0].equalsIgnoreCase(subCommand.getName()))
      .findFirst();
    return matchingSubCommand.map(subCommand -> subCommand.onTabComplete(sender, args))
      .orElseGet(ArrayList::new);
  }
}
