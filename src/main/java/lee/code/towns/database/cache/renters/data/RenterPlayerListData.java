package lee.code.towns.database.cache.renters.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RenterPlayerListData {

    private final ConcurrentHashMap<UUID, Set<String>> rentedChunkListCache = new ConcurrentHashMap<>();

    public void addChunkList(UUID uuid, String chunk) {
        if (rentedChunkListCache.containsKey(uuid)) {
            rentedChunkListCache.get(uuid).add(chunk);
        } else {
            final Set<String> chunks = Collections.synchronizedSet(new HashSet<>());
            chunks.add(chunk);
            rentedChunkListCache.put(uuid, chunks);
        }
    }

    public void removeChunkList(UUID uuid, String chunk) {
        rentedChunkListCache.get(uuid).remove(chunk);
    }

    public void removeAllChunkList(UUID uuid) {
        rentedChunkListCache.remove(uuid);
    }

    public Set<String> getChunkList(UUID uuid) {
        return rentedChunkListCache.get(uuid);
    }

    public boolean hasRentedChunks(UUID uuid) {
        return rentedChunkListCache.containsKey(uuid);
    }

    public int getRentedClaimAmount(UUID uuid) {
        return rentedChunkListCache.get(uuid).size();
    }
}
