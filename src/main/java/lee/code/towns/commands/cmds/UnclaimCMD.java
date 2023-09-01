package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnclaimCMD extends SubCommand {
  private final Towns towns;

  public UnclaimCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "unclaim";
  }

  @Override
  public String getDescription() {
    return "Unclaim the chunk you're standing on.";
  }

  @Override
  public String getSyntax() {
    return "/t unclaim";
  }

  @Override
  public String getPermission() {
    return "towns.command.unclaim";
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
    final CacheManager cacheManager = towns.getCacheManager();
    final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
    final UUID uuid = player.getUniqueId();
    if (!cacheManager.getCacheChunks().isClaimed(chunk)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_NOT_CLAIMED.getComponent(new String[]{chunk})));
      return;
    }
    final UUID owner = cacheManager.getCacheTowns().getTargetTownOwner(uuid);
    if (!cacheManager.getCacheChunks().isChunkOwner(chunk, owner)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_NOT_OWNER.getComponent(new String[]{chunk})));
      return;
    }
    if (!uuid.equals(owner)) {
      final String role = cacheManager.getCacheTowns().getPlayerRoleData().getPlayerRole(owner, uuid);
      if (!cacheManager.getCacheTowns().getRoleData().checkRolePermissionFlag(owner, role, Flag.UNCLAIM)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_NO_PERMISSION.getComponent(null)));
        return;
      }
    }
    if (towns.getAutoClaimManager().isAutoClaiming(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_AUTO_CLAIM_ON.getComponent(null)));
      return;
    }
    if (cacheManager.getCacheChunks().isEstablishedChunk(chunk)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_ESTABLISHED_CHUNK.getComponent(new String[]{chunk})));
      return;
    }
    if (!cacheManager.getCacheChunks().isUnclaimSafe(owner, chunk)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_UNSAFE.getComponent(new String[]{chunk})));
      return;
    }
    if (cacheManager.getCacheRenters().isRentable(chunk)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_RENTABLE.getComponent(new String[]{chunk})));
      return;
    }
    if (cacheManager.getCacheRenters().isRented(chunk)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_RENTED.getComponent(new String[]{chunk, cacheManager.getCacheRenters().getRenterName(chunk)})));
      return;
    }
    cacheManager.getCacheChunks().unclaimChunk(chunk);
    towns.getBorderParticleManager().spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.UNCLAIM, false);
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_UNCLAIM_SUCCESS.getComponent(new String[]{chunk})));
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
