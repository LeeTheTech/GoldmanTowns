package lee.code.towns.database.cache.renters.data;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OwnerListData {

    private final ConcurrentHashMap<UUID, Set<String>> rentChunkListCache = new ConcurrentHashMap<>();

    public void addChunkList(UUID uuid, String chunk) {
        if (rentChunkListCache.containsKey(uuid)) {
            rentChunkListCache.get(uuid).add(chunk);
        } else {
            final Set<String> chunks = ConcurrentHashMap.newKeySet();
            chunks.add(chunk);
            rentChunkListCache.put(uuid, chunks);
        }
    }

    public void removeChunkList(UUID uuid, String chunk) {
        rentChunkListCache.get(uuid).remove(chunk);
    }

    public void removeAllChunkList(UUID uuid) {
        rentChunkListCache.remove(uuid);
    }

    public Set<String> getChunkList(UUID uuid) {
        return rentChunkListCache.get(uuid);
    }

    public boolean hasRentChunks(UUID uuid) {
        return rentChunkListCache.containsKey(uuid);
    }

    public int getRentedClaimAmount(UUID uuid) {
        return rentChunkListCache.get(uuid).size();
    }
}
