package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoMapManager;
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
    return "/towns automap";
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
    final AutoMapManager autoMapManager = towns.getAutoMapManager();
    final UUID uuid = player.getUniqueId();
    if (towns.getAutoClaimManager().isAutoClaiming(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_MAP_AUTO_CLAIMING.getComponent(null)));
      return;
    }
    final boolean active = autoMapManager.isAutoMapping(uuid);
    if (active) autoMapManager.removeAutoMapping(uuid);
    else autoMapManager.setAutoMapping(uuid, ChunkUtil.serializeChunkLocation(player.getLocation().getChunk()));
    final String result = active ? Lang.OFF.getString() : Lang.ON.getString();
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
