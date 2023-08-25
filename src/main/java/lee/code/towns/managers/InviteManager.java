package lee.code.towns.managers;

import lee.code.towns.Towns;
import lee.code.towns.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InviteManager {
    //TODO fix so players can send many invites...
    private final Towns towns;
    public InviteManager(Towns towns) {
        this.towns = towns;
    }

    private final ConcurrentHashMap<UUID, Set<UUID>> townInvites = new ConcurrentHashMap<>();

    private void setInviteRequests(UUID playerID, UUID targetID) {
        if (townInvites.containsKey(playerID)) {
            townInvites.get(playerID).add(targetID);
        } else {
            final Set<UUID> requests = ConcurrentHashMap.newKeySet();
            requests.add(targetID);
            townInvites.put(playerID, requests);
        }
    }

    public boolean hasActiveInvite(UUID playerID, UUID targetID) {
        if (!townInvites.containsKey(playerID)) return false;
        return townInvites.get(playerID).contains(targetID);
    }
    public void setActiveInvite(UUID playerID, UUID targetID) {
        setInviteRequests(playerID, targetID);
        inviteTimeoutTimer(playerID, targetID);
    }
    public void removeActiveInvite(UUID playerID, UUID targetID) {
        townInvites.get(playerID).remove(targetID);
        if (townInvites.get(playerID).isEmpty()) townInvites.remove(playerID);
    }

    public void inviteTimeoutTimer(UUID playerID, UUID targetID) {
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
                removeActiveInvite(playerID, targetID);
            }
        }, 60, TimeUnit.SECONDS);
    }
}
