package lee.code.towns.managers;

import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.Towns;
import lee.code.towns.lang.Lang;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InviteManager {
  private final Towns towns;
  private final ConcurrentHashMap<UUID, Set<UUID>> townInvites = new ConcurrentHashMap<>();

  public InviteManager(Towns towns) {
    this.towns = towns;
  }

  private void setInviteRequests(UUID playerID, UUID targetID) {
    if (townInvites.containsKey(playerID)) {
      townInvites.get(playerID).add(targetID);
    } else {
      Set<UUID> requests = ConcurrentHashMap.newKeySet();
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
        PlayerDataAPI.sendPlayerMessageIfOnline(playerID, Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_TIMEOUT_PLAYER.getComponent(new String[]{PlayerDataAPI.getName(targetID)})));
        PlayerDataAPI.sendPlayerMessageIfOnline(targetID, Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_TIMEOUT_TARGET.getComponent(new String[]{PlayerDataAPI.getName(playerID)})));
        removeActiveInvite(playerID, targetID);
      }
    }, 60, TimeUnit.SECONDS);
  }
}
