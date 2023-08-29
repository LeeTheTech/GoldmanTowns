package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.PvEEvent;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.FlagUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class PvEListener implements Listener {
  private final Towns towns;

  public PvEListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onEntityDamageByEntityListener(EntityDamageByEntityEvent e) {
    if (towns.getData().getMonsterTypes().contains(e.getEntity().getType())) return;
    if (e.getDamager() instanceof Player attacker) {
      if (!(e.getEntity() instanceof Player)) {
        final PvEEvent pveEvent = new PvEEvent(attacker, ChunkUtil.serializeChunkLocation(e.getEntity().getLocation().getChunk()));
        Bukkit.getServer().getPluginManager().callEvent(pveEvent);
        if (pveEvent.isCancelled()) e.setCancelled(true);
      }
    } else if (e.getDamager() instanceof Projectile projectile) {
      if (projectile.getShooter() instanceof Player attacker) {
        final PvEEvent pveEvent = new PvEEvent(attacker, ChunkUtil.serializeChunkLocation(e.getEntity().getLocation().getChunk()));
        Bukkit.getServer().getPluginManager().callEvent(pveEvent);
        if (pveEvent.isCancelled()) e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onVehicleDestroyListener(VehicleDestroyEvent e) {
    if (e.getAttacker() instanceof Player attacker) {
      final PvEEvent pveEvent = new PvEEvent(attacker, ChunkUtil.serializeChunkLocation(e.getVehicle().getLocation().getChunk()));
      Bukkit.getServer().getPluginManager().callEvent(pveEvent);
      if (pveEvent.isCancelled()) e.setCancelled(true);
    }
  }

  @EventHandler
  public void onHangingBreakByEntityListener(HangingBreakByEntityEvent e) {
    if (e.getRemover() instanceof Player attacker) {
      final PvEEvent pveEvent = new PvEEvent(attacker, ChunkUtil.serializeChunkLocation(e.getEntity().getLocation().getChunk()));
      Bukkit.getServer().getPluginManager().callEvent(pveEvent);
      if (pveEvent.isCancelled()) e.setCancelled(true);
    }
  }

  @EventHandler
  public void onPvE(PvEEvent e) {
    final CacheManager cacheManager = towns.getCacheManager();
    final boolean result = cacheManager.checkPlayerLocationFlag(e.getAttacker().getUniqueId(), e.getChunk(), Flag.PVE, true);
    e.setCancelled(result);
    if (result) FlagUtil.sendFlagErrorMessage(e.getAttacker(), Flag.PVE, cacheManager.getChunkTownName(e.getChunk()), cacheManager.getCacheRenters().getRenterName(e.getChunk()));
  }
}
