package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.RedstoneEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class RedstoneListener implements Listener {
  private final Towns towns;

  public RedstoneListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onBlockPistonRetractListener(BlockPistonRetractEvent e) {
    final RedstoneEvent redstoneEvent = new RedstoneEvent(e.getBlock().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(redstoneEvent);
    if (redstoneEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onBlockPistonExtendListener(BlockPistonExtendEvent e) {
    final RedstoneEvent redstoneEvent = new RedstoneEvent(e.getBlock().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(redstoneEvent);
    if (redstoneEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onRedstone(RedstoneEvent e) {
    e.setCancelled(towns.getCacheManager().checkLocationFlag(e.getLocation(), Flag.REDSTONE));
  }
}
