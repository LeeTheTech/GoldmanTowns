package lee.code.towns.listeners;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.events.AutoClaimEvent;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoClaimManager;
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
        final CacheManager cacheManager = towns.getCacheManager();
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        final String chunk = e.getChunk();
        synchronized (synchronizedThreadLock) {
            Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
                if (!cacheManager.getCacheChunks().isConnectedChunk(uuid, chunk)) {
                    autoClaimManager.removeAutoClaiming(uuid);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_CLAIM_RANGE.getComponent(new String[] { Lang.OFF.getString() })));
                    return;
                }
                final int currentChunks = cacheManager.getCacheChunks().getChunkClaims(uuid);
                final int maxChunks = cacheManager.getCacheTowns().getMaxChunkClaims(uuid);
                if (maxChunks < currentChunks + 1) {
                    autoClaimManager.removeAutoClaiming(uuid);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_AUTO_CLAIM_MAX_CLAIMS.getComponent(new String[] { String.valueOf(maxChunks) })));
                    return;
                }
                if (cacheManager.getCacheChunks().isClaimed(chunk)) return;
                cacheManager.getCacheChunks().claimChunk(chunk, uuid);
                towns.getBorderParticleManager().spawnParticleChunkBorder(player.getLocation(), e.getLocation().getChunk(), ChunkRenderType.CLAIM);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CLAIM_SUCCESS.getComponent(new String[] { chunk, String.valueOf(currentChunks + 1), String.valueOf(maxChunks) })));
            });
        }
    }
}
