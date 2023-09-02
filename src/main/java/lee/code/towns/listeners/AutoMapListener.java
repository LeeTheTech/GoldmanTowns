package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.events.AutoMapEvent;
import lee.code.towns.managers.MapManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class AutoMapListener implements Listener {
  private final Towns towns;

  public AutoMapListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onPlayerMoveListener(PlayerMoveEvent e) {
    final MapManager mapManager = towns.getMapManager();
    final UUID uuid = e.getPlayer().getUniqueId();
    if (mapManager.isAutoMapping(uuid)) {
      final Location location = e.getTo();
      final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
      if (mapManager.getLastAutoMapChunkChecked(uuid).equals(chunk)) return;
      mapManager.setLastAutoMapChunkChecked(uuid, chunk);
      final AutoMapEvent autoMapEvent = new AutoMapEvent(e.getPlayer(), location);
      Bukkit.getServer().getPluginManager().callEvent(autoMapEvent);
    }
  }

  @EventHandler
  public void onAutoMap(AutoMapEvent e) {
    Bukkit.getScheduler().runTaskAsynchronously(towns, () -> towns.getMapManager().sendMap(e.getPlayer(), false, 7));
  }
}
