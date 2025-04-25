package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.FireSpreadEvent;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class FireSpreadListener implements Listener {
  private final Towns towns;

  public FireSpreadListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onFireSpreadListener(BlockSpreadEvent e) {
    if (!e.getSource().getType().equals(Material.FIRE)) return;
    FireSpreadEvent fireSpreadEvent = new FireSpreadEvent(e.getBlock().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(fireSpreadEvent);
    if (fireSpreadEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onFireSpread(FireSpreadEvent e) {
    e.setCancelled(towns.getCacheManager().checkLocationFlag(ChunkUtil.serializeChunkLocation(e.getLocation().getChunk()), Flag.FIRE_SPREAD));
  }
}
