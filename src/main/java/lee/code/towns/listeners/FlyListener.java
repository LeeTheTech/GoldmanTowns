package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.events.FlyEvent;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.FlyManager;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
    UUID playerID = e.getPlayer().getUniqueId();
    FlyManager flyManager = towns.getFlyManager();
    if (flyManager.isFlying(playerID)) {
      Location location = e.getTo();
      String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
      if (flyManager.hasChunkChecked(playerID) && flyManager.getLastChunkChecked(playerID).equals(chunk)) return;
      if (towns.getAutoClaimManager().isAutoClaiming(playerID)) return;
      flyManager.setLastChunkChecked(playerID, chunk);
      FlyEvent flyEvent = new FlyEvent(e.getPlayer(), location, chunk);
      Bukkit.getServer().getPluginManager().callEvent(flyEvent);
    }
  }

  @EventHandler
  public void onFly(FlyEvent e) {
    CacheManager cacheManager = towns.getCacheManager();
    Player player = e.getPlayer();
    UUID playerID = player.getUniqueId();
    if (cacheManager.getCacheChunks().isClaimed(e.getChunk())) {
      if (cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
        UUID ownerID = cacheManager.getCacheTowns().getTargetTownOwner(playerID);
        if (cacheManager.getCacheChunks().isChunkOwner(e.getChunk(), ownerID)) {
          return;
        }
      }
    }
    player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20*7, 1));
    synchronized (CoreUtil.getSynchronizedThreadLock()) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        towns.getFlyManager().disableFlying(e.getPlayer());
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLY_OUTSIDE_OF_TOWN.getComponent(new String[]{Lang.OFF.getString()})));
      });
    }
  }
}
