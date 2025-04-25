package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.events.AutoMessageEvent;
import lee.code.towns.managers.AutoMessageManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class AutoMessageListener implements Listener {
  private final Towns towns;

  public AutoMessageListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onAutoMessageListener(PlayerMoveEvent e) {
    AutoMessageManager autoMessageManager = towns.getAutoMessageManager();
    UUID uuid = e.getPlayer().getUniqueId();
    String chunk = ChunkUtil.serializeChunkLocation(e.getPlayer().getLocation().getChunk());
    if (autoMessageManager.isLastChunkChecked(uuid, chunk)) return;
    autoMessageManager.setLastChunkChecked(uuid, chunk);
    AutoMessageEvent autoMessageEvent = new AutoMessageEvent(e.getPlayer(), e.getPlayer().getLocation(), chunk);
    Bukkit.getServer().getPluginManager().callEvent(autoMessageEvent);
  }

  @EventHandler
  public void onAutoMessage(AutoMessageEvent e) {
    CacheManager cacheManager = towns.getCacheManager();
    AutoMessageManager autoMessageManager = towns.getAutoMessageManager();
    Player player = e.getPlayer();
   UUID uuid = player.getUniqueId();
    if (!cacheManager.getCacheChunks().isClaimed(e.getChunk())) {
      autoMessageManager.removeLastTownChecked(uuid);
      return;
    }
    String town = cacheManager.getChunkTownName(e.getChunk());
    if (cacheManager.getCacheRenters().isRentable(e.getChunk())) {
      if (cacheManager.getCacheTowns().hasTownOrJoinedTown(uuid)) {
        UUID owner = cacheManager.getCacheTowns().getTargetTownOwner(uuid);
        if (cacheManager.getCacheTowns().getTownName(owner).equals(town)) {
          autoMessageManager.sendChunkRentableMessage(player, cacheManager.getCacheRenters().getRentPrice(e.getChunk()));
        }
      }
    }
    if (autoMessageManager.isLastTownChecked(uuid, town)) return;
    autoMessageManager.setLastTownChecked(uuid, town);
    autoMessageManager.sendTownMessage(player, town);
  }
}
