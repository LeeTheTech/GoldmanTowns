package lee.code.towns.managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AutoMapManager {
  private final ConcurrentHashMap<UUID, String> autoMapping = new ConcurrentHashMap<>();

  public boolean isAutoMapping(UUID uuid) {
    return autoMapping.containsKey(uuid);
  }

  public void removeAutoMapping(UUID uuid) {
    autoMapping.remove(uuid);
  }

  public void setAutoMapping(UUID uuid, String chunk) {
    autoMapping.put(uuid, chunk);
  }

  public void setLastAutoMapChunkChecked(UUID uuid, String chunk) {
    autoMapping.put(uuid, chunk);
  }

  public String getLastAutoMapChunkChecked(UUID uuid) {
    return autoMapping.get(uuid);
  }
}
