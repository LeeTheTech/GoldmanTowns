package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.BreakEvent;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.FlagUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakListener implements Listener {
  private final Towns towns;

  public BreakListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onBlockBreakListener(BlockBreakEvent e) {
    final BreakEvent breakEvent = new BreakEvent(e.getPlayer(), e.getBlock().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(breakEvent);
    if (breakEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onBreak(BreakEvent e) {
    final CacheManager cacheManager = towns.getCacheManager();
    final String chunk = ChunkUtil.serializeChunkLocation(e.getLocation().getChunk());
    final boolean result = cacheManager.checkPlayerLocationFlag(e.getPlayer().getUniqueId(), chunk, Flag.BREAK, true);
    e.setCancelled(result);
    if (result) FlagUtil.sendFlagErrorMessage(
      e.getPlayer(),
      Flag.BREAK,
      cacheManager.getChunkTownName(chunk),
      cacheManager.getCacheRenters().getRenter(chunk),
      cacheManager.getCacheRenters().getRenterName(chunk)
    );
  }
}
