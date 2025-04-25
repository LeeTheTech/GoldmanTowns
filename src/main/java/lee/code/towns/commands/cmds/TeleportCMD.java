package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportCMD extends SubCommand {
  private final Towns towns;

  public TeleportCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "teleport";
  }

  @Override
  public String getDescription() {
    return "Teleport to a target chunk you own or a town that is public.";
  }

  @Override
  public String getSyntax() {
    return "/t teleport &f<chunk/town> <chunk/town>";
  }

  @Override
  public String getPermission() {
    return "towns.command.teleport";
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
    if (args.length < 3) {
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    UUID playerID = player.getUniqueId();
    String option = args[1].toLowerCase();
    String target = CoreUtil.buildStringFromArgs(args, 2);
    CacheManager cacheManager = towns.getCacheManager();
    switch (option) {
      case "chunk" -> {
        if (!cacheManager.getCacheChunks().isClaimed(target) || !cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_NOT_APART_OF_TOWN.getComponent(null)));
          return;
        }
        if (!cacheManager.getCacheChunks().getChunkOwner(target).equals(cacheManager.getCacheTowns().getTargetTownOwner(playerID))) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_NOT_APART_OF_TOWN.getComponent(null)));
          return;
        }
        ChunkUtil.teleportToMiddleOfChunk(player, target);
      }
      case "town" -> {
        if (!cacheManager.getCacheTowns().getTownNameListData().isTownName(target)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_TOWN_DOES_NOT_EXIST.getComponent(new String[]{target})));
          return;
        }
        UUID owner = cacheManager.getCacheTowns().getTownNameListData().getTownNameOwner(target);
        if (!cacheManager.getCacheTowns().isTownPublic(owner)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_TOWN_PRIVATE.getComponent(new String[]{Lang.PRIVATE.getString()})));
          return;
        }
        player.teleportAsync(cacheManager.getCacheTowns().getTownSpawn(owner)).thenAccept(result -> {
          if (result) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_TELEPORT_TOWN_SUCCESS.getComponent(new String[]{target})));
          else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_TELEPORT_TOWN_FAILED.getComponent(new String[]{target})));
        });
      }
      default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
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
