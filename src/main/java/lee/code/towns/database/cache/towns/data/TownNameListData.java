package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TownNameListData {
  private final CacheTowns cacheTowns;
  private final ConcurrentHashMap<String, UUID> townNameCache = new ConcurrentHashMap<>();

  public TownNameListData(CacheTowns cacheTowns) {
    this.cacheTowns = cacheTowns;
  }

  private void setTownNameCache(String name, UUID uuid) {
    townNameCache.put(name, uuid);
  }

  private void removeTownNameCache(String name) {
    townNameCache.remove(name);
  }

  public void cacheTownName(TownsTable townsTable) {
    if (townsTable.getTown() == null) return;
    setTownNameCache(townsTable.getTown(), townsTable.getUniqueId());
  }

  public void setTownName(String name, UUID uuid, boolean updateDatabase) {
    TownsTable townsTable = cacheTowns.getTownTable(uuid);
    townsTable.setTown(name);
    setTownNameCache(name, uuid);
    if (updateDatabase) cacheTowns.updateTownsDatabase(townsTable);
  }

  public void removeTownName(String name) {
    removeTownNameCache(name);
  }

  public UUID getTownNameOwner(String name) {
    return townNameCache.get(name);
  }

  public boolean isTownName(String name) {
    return townNameCache.containsKey(name);
  }
}
