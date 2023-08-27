package lee.code.towns.listeners;

import lee.code.towns.Towns;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
  private final Towns towns;

  public JoinListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (!towns.getCacheManager().getCacheTowns().hasTownsData(e.getPlayer().getUniqueId())) {
      towns.getCacheManager().getCacheTowns().createPlayerData(e.getPlayer().getUniqueId());
    }
  }
}
