package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.PvPEvent;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.FlagUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPListener implements Listener {
  private final Towns towns;

  public PvPListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onEntityDamageByEntityListener(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player victim) {
      if (e.getDamager() instanceof Player attacker) {
        final PvPEvent pvpEvent = new PvPEvent(attacker, victim, ChunkUtil.serializeChunkLocation(victim.getLocation().getChunk()));
        Bukkit.getServer().getPluginManager().callEvent(pvpEvent);
        if (pvpEvent.isCancelled()) e.setCancelled(true);
      } else if (e.getDamager() instanceof Projectile projectile) {
        if (projectile.getShooter() instanceof Player projectileAttacker) {
          final PvPEvent pvpEvent = new PvPEvent(victim, projectileAttacker, ChunkUtil.serializeChunkLocation(victim.getLocation().getChunk()));
          Bukkit.getServer().getPluginManager().callEvent(pvpEvent);
          if (pvpEvent.isCancelled()) e.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onPvP(PvPEvent e) {
    final CacheManager cacheManager = towns.getCacheManager();
    final boolean result = cacheManager.checkPlayerLocationFlag(e.getVictim().getUniqueId(), e.getChunk(), Flag.PVP, false);
    e.setCancelled(result);
    if (result) {
      FlagUtil.sendFlagErrorMessage(e.getAttacker(), Flag.PVP, cacheManager.getChunkTownName(e.getChunk()), cacheManager.getCacheRenters().getRenterName(e.getChunk()));
      FlagUtil.sendFlagErrorMessage(e.getVictim(), Flag.PVP, cacheManager.getChunkTownName(e.getChunk()), cacheManager.getCacheRenters().getRenterName(e.getChunk()));
    }
  }
}
