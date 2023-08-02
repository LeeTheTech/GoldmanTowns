package lee.code.towns.managers;

import lee.code.towns.Towns;
import lee.code.towns.lang.Lang;
import org.bukkit.Bukkit;
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
                final Player player = Bukkit.getPlayer(playerID);
                final Player target = Bukkit.getPlayer(targetID);
                if (player != null && target != null && player.isOnline()) {
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_TIMEOUT.getComponent(new String[] { target.getName() })));
                }
                removeActiveInvite(playerID);
            }
        }, 60, TimeUnit.SECONDS);
    }
}
