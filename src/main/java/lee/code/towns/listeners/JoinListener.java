package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.CacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class JoinListener implements Listener {

    private final CacheManager cacheManager;

    public JoinListener(Towns towns) {
        this.cacheManager = towns.getCacheManager();
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        if (!cacheManager.getCachePlayers().hasPlayerData(e.getUniqueId())) {
            cacheManager.getCachePlayers().createPlayerData(e.getUniqueId());
        }
    }
}
