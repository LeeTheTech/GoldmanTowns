package lee.code.towns.managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AutoClaimManager {
  private final ConcurrentHashMap<UUID, String> autoClaiming = new ConcurrentHashMap<>();

  public boolean isAutoClaiming(UUID uuid) {
    return autoClaiming.containsKey(uuid);
  }

  public void removeAutoClaiming(UUID uuid) {
    autoClaiming.remove(uuid);
  }

  public void setAutoClaiming(UUID uuid, String chunk) {
    autoClaiming.put(uuid, chunk);
  }

  public void setLastAutoClaimChunkChecked(UUID uuid, String chunk) {
    autoClaiming.put(uuid, chunk);
  }

  public String getLastAutoClaimChunkChecked(UUID uuid) {
    return autoClaiming.get(uuid);
  }
}
