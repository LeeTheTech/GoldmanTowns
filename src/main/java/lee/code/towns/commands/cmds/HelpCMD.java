package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class HelpCMD extends SubCommand {
  private final Towns towns;

  public HelpCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public String getDescription() {
    return "List of commands you can use with this plugin.";
  }

  @Override
  public String getSyntax() {
    return "/t help";
  }

  @Override
  public String getPermission() {
    return "towns.command.help";
  }

  @Override
  public boolean performAsync() {
    return true;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return false;
  }

  @Override
  public void perform(Player player, String[] args) {
    performSender(player, args);
  }

  @Override
  public void performConsole(CommandSender console, String[] args) {
    performSender(console, args);
  }

  @Override
  public void performSender(CommandSender sender, String[] args) {
    int index;
    int page = 0;
    int maxDisplayed = 10;
    if (args.length > 1) {
      if (CoreUtil.isPositiveIntNumber(args[1])) page = Integer.parseInt(args[1]);
    }
    int position = page * maxDisplayed + 1;

    Map<SubCommand, String> commands = new HashMap<>();
    for (SubCommand subCommand : towns.getCommandManager().getSubCommandList()){
      if (sender.hasPermission(subCommand.getPermission())) commands.put(subCommand, subCommand.getName());
    }
    Map<SubCommand, String> sortedCommands = CoreUtil.sortByValue(commands, Comparator.naturalOrder());
    List<SubCommand> sortedCommandList = new ArrayList<>(sortedCommands.keySet());

    List<Component> lines = new ArrayList<>();
    lines.add(Lang.COMMAND_HELP_TITLE.getComponent(null));
    lines.add(Component.text(""));

    for (int i = 0; i < maxDisplayed; i++) {
      index = maxDisplayed * page + i;
      if (index >= sortedCommandList.size()) break;
      SubCommand subCommand = sortedCommandList.get(index);
      Component helpSubCommand = Lang.COMMAND_HELP_SUB_COMMAND.getComponent(new String[]{String.valueOf(position), subCommand.getSyntax()})
        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, CoreUtil.getTextBeforeCharacter(subCommand.getSyntax(), '&')))
        .hoverEvent(Lang.COMMAND_HELP_SUB_COMMAND_HOVER.getComponent(new String[]{subCommand.getDescription()}));
      lines.add(helpSubCommand);
      position++;
    }

    if (lines.size() == 2) return;
    lines.add(Component.text(""));
    lines.add(CoreUtil.createPageSelectionComponent("/towns help", page));
    for (Component line : lines) sender.sendMessage(line);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return new ArrayList<>();
  }
}
