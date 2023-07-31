package lee.code.towns.listeners;

import lee.code.towns.Towns;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class JoinListener implements Listener {

    private final Towns towns;

    public JoinListener(Towns towns) {
        this.towns = towns;
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        if (!towns.getCacheManager().getCachePlayers().hasPlayerData(e.getUniqueId())) {
            towns.getCacheManager().getCachePlayers().createPlayerData(e.getUniqueId());
        }
    }
}
