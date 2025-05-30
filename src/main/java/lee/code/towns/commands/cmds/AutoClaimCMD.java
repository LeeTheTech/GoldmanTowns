package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutoClaimCMD extends SubCommand {
  private final Towns towns;

  public AutoClaimCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "autoclaim";
  }

  @Override
  public String getDescription() {
    return "Toggle auto claim to claim chunks for your town as you walk.";
  }

  @Override
  public String getSyntax() {
    return "/t autoclaim";
  }

  @Override
  public String getPermission() {
    return "towns.command.autoclaim";
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
    CacheManager cacheManager = towns.getCacheManager();
    AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
    UUID playerID = player.getUniqueId();
    if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }
    UUID ownerID = cacheManager.getCacheTowns().getTargetTownOwner(playerID);
    if (!playerID.equals(ownerID)) {
      String role = cacheManager.getCacheTowns().getPlayerRoleData().getPlayerRole(ownerID, playerID);
      if (!cacheManager.getCacheTowns().getRoleData().checkRolePermissionFlag(ownerID, role, Flag.CLAIM)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_NO_PERMISSION.getComponent(null)));
        return;
      }
    }
    boolean active = autoClaimManager.isAutoClaiming(playerID);
    if (active) {
      autoClaimManager.removeAutoClaiming(playerID);
    } else {
      String chunkString = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
      if (!cacheManager.getCacheChunks().isClaimed(chunkString) || !cacheManager.getCacheChunks().isChunkOwner(chunkString, ownerID)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_CLAIM_NOT_OWNER.getComponent(null)));
        return;
      }
      autoClaimManager.setAutoClaiming(playerID, chunkString);
    }
    String result = active ? Lang.OFF.getString() : Lang.ON.getString();
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_AUTO_CLAIM_SUCCESS.getComponent(new String[]{result})));
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
