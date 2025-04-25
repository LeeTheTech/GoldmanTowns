package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.MapManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutoMapCMD extends SubCommand {
  private final Towns towns;

  public AutoMapCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "automap";
  }

  @Override
  public String getDescription() {
    return "Automatically show map each new chunk you stand on.";
  }

  @Override
  public String getSyntax() {
    return "/t automap";
  }

  @Override
  public String getPermission() {
    return "towns.command.automap";
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
    MapManager mapManager = towns.getMapManager();
    UUID playerID = player.getUniqueId();
    boolean active = mapManager.isAutoMapping(playerID);
    if (active) mapManager.removeAutoMapping(playerID);
    else mapManager.setAutoMapping(playerID, ChunkUtil.serializeChunkLocation(player.getLocation().getChunk()));
    String result = active ? Lang.OFF.getString() : Lang.ON.getString();
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_AUTO_MAP_SUCCESS.getComponent(new String[]{result})));
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
