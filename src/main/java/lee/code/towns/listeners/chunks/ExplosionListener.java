package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.ExplosionEvent;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;

public class ExplosionListener implements Listener {
  private final Towns towns;

  public ExplosionListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onEntityExplodeListener(EntityExplodeEvent e) {
    for (Block block : new ArrayList<>(e.blockList())) {
      final ExplosionEvent explosionEvent = new ExplosionEvent(block.getLocation());
      Bukkit.getServer().getPluginManager().callEvent(explosionEvent);
      if (explosionEvent.isCancelled()) e.blockList().remove(block);
    }
  }

  @EventHandler
  public void onEntityExplodeListener(EntityChangeBlockEvent e) {
    if (e.getEntity() instanceof Wither || e.getEntity() instanceof EnderDragon) {
      final ExplosionEvent explosionEvent = new ExplosionEvent(e.getBlock().getLocation());
      Bukkit.getServer().getPluginManager().callEvent(explosionEvent);
      if (explosionEvent.isCancelled()) e.setCancelled(true);
    }
  }

  @EventHandler
  public void onExplosion(ExplosionEvent e) {
    e.setCancelled(towns.getCacheManager().checkLocationFlag(ChunkUtil.serializeChunkLocation(e.getLocation().getChunk()), Flag.EXPLOSION));
  }
}
