package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.BuildEvent;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.FlagUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class BuildListener implements Listener {
  private final Towns towns;

  public BuildListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onBlockPlaceListener(BlockPlaceEvent e) {
    final BuildEvent buildEvent = new BuildEvent(e.getPlayer(), ChunkUtil.serializeChunkLocation(e.getBlockPlaced().getLocation().getChunk()));
    Bukkit.getServer().getPluginManager().callEvent(buildEvent);
    if (buildEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onEntityPlaceEventListener(EntityPlaceEvent e) {
    final BuildEvent buildEvent = new BuildEvent(e.getPlayer(), ChunkUtil.serializeChunkLocation(e.getBlock().getLocation().getChunk()));
    Bukkit.getServer().getPluginManager().callEvent(buildEvent);
    if (buildEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onPlayerPortalListener(PlayerPortalEvent e) {
    if (!e.getCanCreatePortal()) return;
    final BuildEvent buildEvent = new BuildEvent(e.getPlayer(), ChunkUtil.serializeChunkLocation(e.getTo().getChunk()));
    Bukkit.getServer().getPluginManager().callEvent(buildEvent);
    if (buildEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onBuild(BuildEvent e) {
    final CacheManager cacheManager = towns.getCacheManager();
    final boolean result = cacheManager.checkPlayerLocationFlag(e.getPlayer().getUniqueId(), e.getChunk(), Flag.BUILD, true);
    e.setCancelled(result);
    if (result) FlagUtil.sendFlagErrorMessage(e.getPlayer(), Flag.BUILD, cacheManager.getChunkTownName(e.getChunk()), cacheManager.getCacheRenters().getRenterName(e.getChunk()));
  }
}
