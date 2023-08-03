package lee.code.towns.managers;

import lee.code.towns.Towns;
import lee.code.towns.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InviteManager {
    private final Towns towns;

    public InviteManager(Towns towns) {
        this.towns = towns;
    }

    private final ConcurrentHashMap<UUID, UUID> townInvites = new ConcurrentHashMap<>();

    public boolean hasActiveInvite(UUID playerID, UUID targetID) {
        if (!townInvites.containsKey(playerID)) return false;
        return townInvites.get(playerID).equals(targetID);
    }
    public void setActiveInvite(UUID playerID, UUID targetID) {
        townInvites.put(playerID, targetID);
        teleportTimeoutTimer(playerID, targetID);
    }
    public void removeActiveInvite(UUID player) {
        townInvites.remove(player);
    }

    public void teleportTimeoutTimer(UUID playerID, UUID targetID) {
        Bukkit.getServer().getAsyncScheduler().runDelayed(towns, scheduledTask -> {
            if (hasActiveInvite(playerID, targetID)) {
                final OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(playerID);
                final OfflinePlayer oTarget = Bukkit.getOfflinePlayer(targetID);
                if (oPlayer.isOnline()) {
                    final Player player = oPlayer.getPlayer();
                    if (player != null) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_TIMEOUT_PLAYER.getComponent(new String[] { oTarget.getName() })));
                }
                if (oTarget.isOnline()) {
                    final Player target = oTarget.getPlayer();
                    if (target != null) target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_TIMEOUT_TARGET.getComponent(new String[] { oPlayer.getName() })));
                }
                removeActiveInvite(playerID);
            }
        }, 60, TimeUnit.SECONDS);
    }
}
