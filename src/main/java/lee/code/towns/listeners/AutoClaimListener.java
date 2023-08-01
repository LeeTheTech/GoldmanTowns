package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.events.AutoClaimEvent;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.managers.BorderParticleManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class AutoClaimListener implements Listener {

    private final Towns towns;
    private final Object synchronizedThreadLock = new Object();

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
            final AutoClaimEvent autoClaimEvent = new AutoClaimEvent(e.getPlayer(), location, chunk);
            Bukkit.getServer().getPluginManager().callEvent(autoClaimEvent);
        }
    }

    @EventHandler
    public void onAutoClaim(AutoClaimEvent e) {
        final AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        final String chunk = e.getChunk();
        synchronized (synchronizedThreadLock) {
            Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
                final CacheManager cacheManager = towns.getCacheManager();
                final BorderParticleManager borderParticleManager = towns.getBorderParticleManager();

                if (!cacheManager.getCacheChunks().isConnectedChunk(uuid, chunk)) {
                    autoClaimManager.removeAutoClaiming(uuid);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_CLAIM_RANGE.getComponent(new String[] { Lang.OFF.getString() })));
                    return;
                }

                if (cacheManager.getCacheChunks().isClaimed(chunk)) return;
                cacheManager.getCacheChunks().claim(chunk, uuid);
                borderParticleManager.spawnParticleChunkBorder(player.getLocation(), e.getLocation().getChunk(), ChunkRenderType.CLAIM);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CLAIM_SUCCESS.getComponent(new String[] { chunk })));
            });
        }
    }
}
