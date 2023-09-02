package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.AutoClaimEvent;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class AutoClaimListener implements Listener {
  private final Towns towns;

  public AutoClaimListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onPlayerMoveListener(PlayerMoveEvent e) {
    final AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
    final UUID uuid = e.getPlayer().getUniqueId();
    if (autoClaimManager.isAutoClaiming(uuid)) {
      final Location location = e.getTo();
      final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
      if (autoClaimManager.getLastAutoClaimChunkChecked(uuid).equals(chunk)) return;
      autoClaimManager.setLastAutoClaimChunkChecked(uuid, chunk);
      final AutoClaimEvent autoClaimEvent = new AutoClaimEvent(e.getPlayer(), location, chunk);
      Bukkit.getServer().getPluginManager().callEvent(autoClaimEvent);
    }
  }

  @EventHandler
  public void onAutoClaim(AutoClaimEvent e) {
    final Player player = e.getPlayer();
    final UUID playerID = e.getPlayer().getUniqueId();
    final String chunk = e.getChunk();
    synchronized (CoreUtil.getSynchronizedThreadLock()) {
      Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
        final CacheManager cacheManager = towns.getCacheManager();
        final AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
        if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
          towns.getAutoClaimManager().removeAutoClaiming(playerID);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_CLAIM_TOWN_DOES_NOT_EXIST.getComponent(new String[]{Lang.OFF.getString()})));
          return;
        }
        if (cacheManager.getCacheChunks().isClaimed(chunk)) return;
        final UUID ownerID = cacheManager.getCacheTowns().getTargetTownOwner(playerID);
        if (!playerID.equals(ownerID)) {
          final String role = cacheManager.getCacheTowns().getPlayerRoleData().getPlayerRole(ownerID, playerID);
          if (!cacheManager.getCacheTowns().getRoleData().checkRolePermissionFlag(ownerID, role, Flag.CLAIM)) {
            towns.getAutoClaimManager().removeAutoClaiming(playerID);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_CLAIM_NO_PERMISSION.getComponent(null)));
            return;
          }
        }
        final int currentChunks = cacheManager.getCacheChunks().getChunkListData().getChunkClaims(ownerID);
        final int maxChunks = cacheManager.getCacheTowns().getMaxChunkClaims(ownerID);
        if (maxChunks < currentChunks + 1) {
          towns.getAutoClaimManager().removeAutoClaiming(playerID);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_MAX_CLAIMS.getComponent(new String[]{String.valueOf(maxChunks)})));
          return;
        }
        if (!cacheManager.getCacheChunks().isConnectedChunk(ownerID, chunk)) {
          autoClaimManager.removeAutoClaiming(playerID);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CLAIM_NOT_CONNECTED_CHUNK.getComponent(new String[]{chunk})));
          return;
        }
        cacheManager.getCacheChunks().claimChunk(chunk, ownerID);
        towns.getBorderParticleManager().spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.CLAIM, false);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CLAIM_SUCCESS.getComponent(new String[]{chunk, String.valueOf(currentChunks + 1), String.valueOf(maxChunks)})));
      });
    }
  }
}
