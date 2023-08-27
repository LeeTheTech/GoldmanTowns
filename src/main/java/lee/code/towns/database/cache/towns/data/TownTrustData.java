package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TownTrustData {
  private final CacheTowns cacheTowns;
  private final ConcurrentHashMap<UUID, Set<UUID>> trustedCache = new ConcurrentHashMap<>();

  public TownTrustData(CacheTowns cacheTowns) {
    this.cacheTowns = cacheTowns;
  }

  private void setPlayerTrustedCache(UUID uuid, UUID trusted) {
    if (trustedCache.containsKey(uuid)) {
      trustedCache.get(uuid).add(trusted);
    } else {
      final Set<UUID> trustedPlayers = ConcurrentHashMap.newKeySet();
      trustedPlayers.add(trusted);
      trustedCache.put(uuid, trustedPlayers);
    }
  }

  private void removePlayerTrustedCache(UUID uuid, UUID target) {
    trustedCache.get(uuid).remove(target);
    if (trustedCache.get(uuid).isEmpty()) trustedCache.remove(uuid);
  }

  public void cacheTrustedPlayers(TownsTable townsTable) {
    if (townsTable.getTrustedPlayers() == null) return;
    final String[] players = townsTable.getTrustedPlayers().split(",");
    for (String player : players) {
      setPlayerTrustedCache(townsTable.getUniqueId(), UUID.fromString(player));
    }
  }

  public void addTrusted(UUID uuid, UUID trust, boolean updateDatabase) {
    final TownsTable townsTable = cacheTowns.getTownTable(uuid);
    if (townsTable.getTrustedPlayers() == null) townsTable.setTrustedPlayers(String.valueOf(trust));
    else townsTable.setTrustedPlayers(townsTable.getTrustedPlayers() + "," + trust);
    setPlayerTrustedCache(uuid, trust);
    if (updateDatabase) cacheTowns.updateTownsDatabase(townsTable);
  }

  public void removeTrusted(UUID uuid, UUID target, boolean updateDatabase) {
    final TownsTable townsTable = cacheTowns.getTownTable(uuid);
    final Set<String> trustedPlayers = Collections.synchronizedSet(new HashSet<>(List.of(townsTable.getTrustedPlayers().split(","))));
    trustedPlayers.remove(String.valueOf(target));
    if (trustedPlayers.isEmpty()) townsTable.setTrustedPlayers(null);
    else townsTable.setTrustedPlayers(StringUtils.join(trustedPlayers, ","));
    removePlayerTrustedCache(uuid, target);
    if (updateDatabase) cacheTowns.updateTownsDatabase(townsTable);
  }

  public boolean isTrusted(UUID uuid, UUID target) {
    if (!trustedCache.containsKey(uuid)) return false;
    return trustedCache.get(uuid).contains(target);
  }
}
