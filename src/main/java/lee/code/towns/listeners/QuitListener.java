package lee.code.towns.listeners;

import lee.code.towns.Towns;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitListener implements Listener {

    private final Towns towns;

    public QuitListener(Towns towns) {
        this.towns = towns;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        if (towns.getAutoClaimManager().isAutoClaiming(uuid)) {
            towns.getAutoClaimManager().removeAutoClaiming(uuid);
        }
        if (towns.getBorderParticleManager().hasBorderActive(uuid)) {
            towns.getBorderParticleManager().stopBorder(uuid);
        }
    }
}
