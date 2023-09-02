package lee.code.towns.managers;

import lee.code.towns.Towns;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlyManager {
  private final Towns towns;
  private final ConcurrentHashMap<UUID, String> flyingPlayers = new ConcurrentHashMap<>();

  public FlyManager(Towns towns) {
    this.towns = towns;
  }

  public void removeFlying(UUID uuid) {
    flyingPlayers.remove(uuid);
  }

  public boolean isFlying(UUID uuid) {
    return towns.getCacheManager().getCacheTowns().isFlying(uuid);
  }

  public void setFlying(Player player) {
    towns.getCacheManager().getCacheTowns().setFlying(player.getUniqueId(), true);
    player.setAllowFlight(true);
    player.setFlying(true);
  }

  public void disableFlying(Player player) {
    towns.getCacheManager().getCacheTowns().setFlying(player.getUniqueId(), false);
    player.setAllowFlight(false);
    player.setFlying(false);
    flyingPlayers.remove(player.getUniqueId());
  }

  public void setLastChunkChecked(UUID uuid, String chunk) {
    flyingPlayers.put(uuid, chunk);
  }

  public String getLastChunkChecked(UUID uuid) {
    return flyingPlayers.get(uuid);
  }

  public boolean hasChunkChecked(UUID uuid) {
    return flyingPlayers.containsKey(uuid);
  }
}
