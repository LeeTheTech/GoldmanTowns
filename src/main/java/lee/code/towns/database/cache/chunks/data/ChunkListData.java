package lee.code.towns.database.cache.chunks.data;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkListData {

    private final ConcurrentHashMap<UUID, Set<String>> playerChunkListCache = new ConcurrentHashMap<>();

    public void addChunkList(UUID uuid, String chunk) {
        if (playerChunkListCache.containsKey(uuid)) {
            playerChunkListCache.get(uuid).add(chunk);
        } else {
            final Set<String> chunks = ConcurrentHashMap.newKeySet();
            chunks.add(chunk);
            playerChunkListCache.put(uuid, chunks);
        }
    }

    public void removeChunkList(UUID uuid, String chunk) {
        playerChunkListCache.get(uuid).remove(chunk);
    }

    public void removeAllChunkList(UUID uuid) {
        playerChunkListCache.remove(uuid);
    }

    public Set<String> getChunkList(UUID uuid) {
        return playerChunkListCache.get(uuid);
    }

    public boolean hasClaimedChunks(UUID uuid) {
        return playerChunkListCache.containsKey(uuid);
    }

    public int getChunkClaims(UUID uuid) {
        return playerChunkListCache.get(uuid).size();
    }

}
