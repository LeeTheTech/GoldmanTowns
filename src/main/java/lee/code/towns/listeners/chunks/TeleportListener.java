package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.TeleportEvent;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.FlagUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {
  private final Towns towns;

  public TeleportListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onPlayerTeleportListener(PlayerTeleportEvent e) {
    if (!e.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)) return;
    final TeleportEvent teleportEvent = new TeleportEvent(e.getPlayer(), ChunkUtil.serializeChunkLocation(e.getTo().getChunk()));
    Bukkit.getServer().getPluginManager().callEvent(teleportEvent);
    if (teleportEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onPlayerTeleportEnderPearlListener(ProjectileHitEvent e) {
    if (e.getEntity() instanceof EnderPearl enderPearl) {
      if (e.getEntity().getShooter() instanceof Player player) {
        final TeleportEvent teleportEvent = new TeleportEvent(player, ChunkUtil.serializeChunkLocation(enderPearl.getLocation().getChunk()));
        Bukkit.getServer().getPluginManager().callEvent(teleportEvent);
        if (teleportEvent.isCancelled()) enderPearl.remove();
      }
    }
  }

  @EventHandler
  public void onTeleport(TeleportEvent e) {
    final CacheManager cacheManager = towns.getCacheManager();
    final boolean result = cacheManager.checkPlayerLocationFlag(e.getPlayer().getUniqueId(), e.getChunk(), Flag.TELEPORT, true);
    e.setCancelled(result);
    if (result) FlagUtil.sendFlagErrorMessage(e.getPlayer(), Flag.TELEPORT, cacheManager.getChunkTownName(e.getChunk()), cacheManager.getCacheRenters().getRenterName(e.getChunk()));
  }
}
