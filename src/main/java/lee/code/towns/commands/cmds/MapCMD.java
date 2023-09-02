package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MapCMD extends SubCommand {
  private final Towns towns;

  public MapCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "map";
  }

  @Override
  public String getDescription() {
    return "Displays a chat map view of the chunks around you.";
  }

  @Override
  public String getSyntax() {
    return "/t map";
  }

  @Override
  public String getPermission() {
    return "towns.command.map";
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
    towns.getMapManager().sendMap(player, true, 12);
  }

  @Override
  public void performConsole(CommandSender console, String[] args) {
    console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
  }

  @Override
  public void performSender(CommandSender sender, String[] args) {
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return new ArrayList<>();
  }
}
