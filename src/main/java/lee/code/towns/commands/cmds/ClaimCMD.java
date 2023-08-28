package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.managers.BorderParticleManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ClaimCMD extends SubCommand {
  private final Towns towns;

  public ClaimCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "claim";
  }

  @Override
  public String getDescription() {
    return "Claim the chunk you're standing on.";
  }

  @Override
  public String getSyntax() {
    return "/towns claim";
  }

  @Override
  public String getPermission() {
    return "towns.command.claim";
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
    final BorderParticleManager borderParticleManager = towns.getBorderParticleManager();
    final AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
    final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
    final UUID uuid = player.getUniqueId();
    if (!cacheManager.getCacheTowns().hasTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
      return;
    }
    if (cacheManager.getCacheChunks().isClaimed(chunk)) {
      final String chunkTown = cacheManager.getCacheTowns().getTownName(cacheManager.getCacheChunks().getChunkOwner(chunk));
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_ALREADY_CLAIMED.getComponent(new String[]{chunk, chunkTown})));
      return;
    }
    final int currentChunks = cacheManager.getCacheChunks().getChunkListData().getChunkClaims(uuid);
    final int maxChunks = cacheManager.getCacheTowns().getMaxChunkClaims(uuid);
    if (maxChunks < currentChunks + 1) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_MAX_CLAIMS.getComponent(new String[]{String.valueOf(maxChunks)})));
      return;
    }
    if (args.length > 1 && args[1].equalsIgnoreCase("outpost")) {
      final int currentOutposts = cacheManager.getCacheChunks().getChunkOutpostData().getOutpostAmount(uuid);
      final int maxOutposts = cacheManager.getCacheChunks().getChunkOutpostData().getMaxOutpostAmount();
      if (maxOutposts < currentOutposts + 1) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_MAX_OUTPOSTS.getComponent(new String[]{String.valueOf(maxChunks)})));
        return;
      }
      if (cacheManager.getCacheChunks().isConnectedChunk(uuid, chunk)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_OUTPOST_CONNECTED.getComponent(new String[]{String.valueOf(maxChunks)})));
        return;
      }
      cacheManager.getCacheChunks().claimOutpost(chunk, uuid);
      borderParticleManager.spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.CLAIM, false);
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CLAIM_OUTPOST_SUCCESS.getComponent(new String[]{chunk, String.valueOf(currentOutposts + 1), String.valueOf(maxOutposts)})));
      return;
    }
    if (!cacheManager.getCacheChunks().isConnectedChunk(uuid, chunk)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_NOT_CONNECTED_CHUNK.getComponent(new String[]{chunk})));
      if (autoClaimManager.isAutoClaiming(uuid)) {
        autoClaimManager.removeAutoClaiming(uuid);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_CLAIM_RANGE.getComponent(new String[]{Lang.OFF.getString()})));
      }
      return;
    }
    cacheManager.getCacheChunks().claimChunk(chunk, uuid);
    borderParticleManager.spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.CLAIM, false);
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CLAIM_SUCCESS.getComponent(new String[]{chunk, String.valueOf(currentChunks + 1), String.valueOf(maxChunks)})));
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
