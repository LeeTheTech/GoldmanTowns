package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.commands.CommandManager;
import lee.code.towns.events.AutoClaimEvent;
import lee.code.towns.events.AutoMapEvent;
import lee.code.towns.managers.AutoMapManager;
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
    final AutoMapManager autoMapManager = towns.getAutoMapManager();
    final UUID uuid = e.getPlayer().getUniqueId();
    if (autoMapManager.isAutoMapping(uuid)) {
      final Location location = e.getTo();
      final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
      if (autoMapManager.getLastAutoMapChunkChecked(uuid).equals(chunk)) return;
      autoMapManager.setLastAutoMapChunkChecked(uuid, chunk);
      final AutoMapEvent autoMapEvent = new AutoMapEvent(e.getPlayer(), location);
      Bukkit.getServer().getPluginManager().callEvent(autoMapEvent);
    }
  }

  @EventHandler
  public void onAutoMap(AutoMapEvent e) {
    final CommandManager commandManager = towns.getCommandManager();
    commandManager.performAsync(e.getPlayer(), commandManager.getSubCommand("map"), new String[]{});
  }
}
