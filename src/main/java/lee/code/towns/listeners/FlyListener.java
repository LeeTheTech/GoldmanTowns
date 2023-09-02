package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.commands.CommandManager;
import lee.code.towns.database.CacheManager;
import lee.code.towns.events.FlyEvent;
import lee.code.towns.managers.FlyManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class FlyListener implements Listener {
  private final Towns towns;

  public FlyListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onPlayerFlyListener(PlayerMoveEvent e) {
    final UUID playerID = e.getPlayer().getUniqueId();
    final FlyManager flyManager = towns.getFlyManager();
    if (flyManager.isFlying(playerID)) {
      final Location location = e.getTo();
      final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
      if (flyManager.hasChunkChecked(playerID) && flyManager.getLastChunkChecked(playerID).equals(chunk)) return;
      if (towns.getAutoClaimManager().isAutoClaiming(playerID)) return;
      flyManager.setLastChunkChecked(playerID, chunk);
      final FlyEvent flyEvent = new FlyEvent(e.getPlayer(), location, chunk);
      Bukkit.getServer().getPluginManager().callEvent(flyEvent);
    }
  }

  @EventHandler
  public void onFly(FlyEvent e) {
    final CacheManager cacheManager = towns.getCacheManager();
    final UUID playerID = e.getPlayer().getUniqueId();
    if (cacheManager.getCacheChunks().isClaimed(e.getChunk())) {
      if (cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
        final UUID ownerID = cacheManager.getCacheTowns().getTargetTownOwner(playerID);
        if (cacheManager.getCacheChunks().isChunkOwner(e.getChunk(), ownerID)) {
          return;
        }
      }
    }
    e.getPlayer().addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20*7, 1));
    final CommandManager commandManager = towns.getCommandManager();
    commandManager.performAsync(e.getPlayer(), commandManager.getSubCommand("fly"), new String[]{"fly", "off"});
  }
}
