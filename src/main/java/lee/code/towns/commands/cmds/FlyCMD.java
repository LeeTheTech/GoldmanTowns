package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.FlyManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlyCMD extends SubCommand {
  private final Towns towns;

  public FlyCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "fly";
  }

  @Override
  public String getDescription() {
    return "Toggle flight within town chunks.";
  }

  @Override
  public String getSyntax() {
    return "/t fly &f<off>";
  }

  @Override
  public String getPermission() {
    return "towns.command.fly";
  }

  @Override
  public boolean performAsync() {
    return true;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return true;
  }

  @Override
  public void perform(Player player, String[] args) {
    final UUID playerID = player.getUniqueId();
    final FlyManager flyManager = towns.getFlyManager();
    if (args.length > 1) {
      if (args[1].equalsIgnoreCase("off")) {
        flyManager.disableFlying(player);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_FLY_OFF_SUCCESS.getComponent(new String[]{Lang.OFF.getString()})));
        return;
      }
    }
    if (flyManager.isFlying(playerID)) {
      flyManager.disableFlying(player);
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_FLY_SUCCESS.getComponent(new String[]{Lang.OFF.getString()})));
    } else {
      final CacheManager cacheManager = towns.getCacheManager();
      if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
        return;
      }
      final UUID ownerID = cacheManager.getCacheTowns().getTargetTownOwner(playerID);
      final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
      if (!cacheManager.getCacheChunks().isClaimed(chunk) || !cacheManager.getCacheChunks().isChunkOwner(chunk, ownerID)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLY_NOT_TOWN_CHUNK.getComponent(null)));
        return;
      }
      flyManager.setFlying(player);
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_FLY_SUCCESS.getComponent(new String[]{Lang.ON.getString()})));
    }
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
