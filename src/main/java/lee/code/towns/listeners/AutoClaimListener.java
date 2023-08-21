package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.commands.CommandManager;
import lee.code.towns.events.AutoClaimEvent;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class AutoClaimListener implements Listener {

    private final Towns towns;

    public AutoClaimListener(Towns towns) {
        this.towns = towns;
    }

    @EventHandler
    public void onPlayerMoveListener(PlayerMoveEvent e) {
        final AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
        final UUID uuid = e.getPlayer().getUniqueId();
        if (autoClaimManager.isAutoClaiming(uuid)) {
            final Location location = e.getTo();
            final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
            if (autoClaimManager.getLastAutoClaimChunkChecked(uuid).equals(chunk)) return;
            autoClaimManager.setLastAutoClaimChunkChecked(uuid, chunk);
            final AutoClaimEvent autoClaimEvent = new AutoClaimEvent(e.getPlayer(), location);
            Bukkit.getServer().getPluginManager().callEvent(autoClaimEvent);
        }
    }

    @EventHandler
    public void onAutoClaim(AutoClaimEvent e) {
        final CommandManager commandManager = towns.getCommandManager();
        commandManager.performAsync(e.getPlayer(), commandManager.getSubCommand("claim"), new String[] {});
    }
}
