package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.IceMeltEvent;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

public class IceMeltListener implements Listener {
  private final Towns towns;

  public IceMeltListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onIceMeltListener(BlockFadeEvent e) {
    final Material material = e.getBlock().getType();
    if (!material.equals(Material.ICE) && !material.equals(Material.BLUE_ICE) && !material.equals(Material.PACKED_ICE) && !material.equals(Material.SNOW) && !material.equals(Material.POWDER_SNOW)) return;
    final IceMeltEvent iceMeltEvent = new IceMeltEvent(e.getBlock().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(iceMeltEvent);
    if (iceMeltEvent.isCancelled()) e.setCancelled(true);

  }

  @EventHandler
  public void onIceMelt(IceMeltEvent e) {
    e.setCancelled(towns.getCacheManager().checkLocationFlag(ChunkUtil.serializeChunkLocation(e.getLocation().getChunk()), Flag.ICE_MELT));
  }
}
