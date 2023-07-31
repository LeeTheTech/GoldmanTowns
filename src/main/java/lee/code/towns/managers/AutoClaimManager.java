package lee.code.towns.managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AutoClaimManager {

    private final ConcurrentHashMap<UUID, String> autoClaiming = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Object> autoClaimLock = new ConcurrentHashMap<>();

    public Object getAutoClaimLock(UUID uuid) {
        autoClaimLock.computeIfAbsent(uuid, key -> new Object());
        return autoClaimLock.get(uuid);
    }

    public void removeAutoClaimLock(UUID uuid) {
        autoClaimLock.remove(uuid);
    }

    public boolean isAutoClaiming(UUID uuid) {
        return autoClaiming.containsKey(uuid);
    }

    public void removeAutoClaiming(UUID uuid) {
        autoClaiming.remove(uuid);
    }

    public String getLastAutoClaimChunk(UUID uuid) {
        return autoClaiming.get(uuid);
    }

    public void setAutoClaiming(UUID uuid, String chunk) {
        autoClaiming.put(uuid, chunk);
    }

    public void setLastAutoClaim(UUID uuid, String chunk) {
        autoClaiming.put(uuid, chunk);
    }
}
