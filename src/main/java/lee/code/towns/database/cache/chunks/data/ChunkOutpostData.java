package lee.code.towns.database.cache.chunks.data;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkOutpostData {
  private final ConcurrentHashMap<UUID, Set<String>> outpostCache = new ConcurrentHashMap<>();

  public void addChunkList(UUID uuid, String chunk) {
    if (outpostCache.containsKey(uuid)) {
      outpostCache.get(uuid).add(chunk);
    } else {
      Set<String> chunks = ConcurrentHashMap.newKeySet();
      chunks.add(chunk);
      outpostCache.put(uuid, chunks);
    }
  }

  public Set<String> getChunkList(UUID uuid) {
    if (!outpostCache.containsKey(uuid)) return ConcurrentHashMap.newKeySet();
    return outpostCache.get(uuid);
  }

  public void removeChunkList(UUID uuid, String chunk) {
    outpostCache.get(uuid).remove(chunk);
  }

  public void removeAllChunkList(UUID uuid) {
    outpostCache.remove(uuid);
  }

  public boolean isOutpostOwner(UUID uuid, String chunk) {
    if (uuid == null) return false;
    if (!outpostCache.containsKey(uuid)) return false;
    return outpostCache.get(uuid).contains(chunk);
  }

  public int getOutpostAmount(UUID uuid) {
    if (!outpostCache.containsKey(uuid)) return 0;
    return outpostCache.get(uuid).size();
  }

  public int getMaxOutpostAmount() {
    return 5;
  }
}
