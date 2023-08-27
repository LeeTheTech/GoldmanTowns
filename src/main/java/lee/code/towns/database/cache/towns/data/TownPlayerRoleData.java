package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TownPlayerRoleData {
  private final CacheTowns cacheTowns;
  private final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, String>> playerRoleCache = new ConcurrentHashMap<>();

  public TownPlayerRoleData(CacheTowns cacheTowns) {
    this.cacheTowns = cacheTowns;
  }

  private void setPlayerRoleCache(UUID uuid, UUID target, String role) {
    if (playerRoleCache.containsKey(uuid)) {
      playerRoleCache.get(uuid).put(target, role);
    } else {
      final ConcurrentHashMap<UUID, String> roles = new ConcurrentHashMap<>();
      roles.put(target, role);
      playerRoleCache.put(uuid, roles);
    }
  }

  private void removePlayerRoleCache(UUID uuid, UUID target) {
    playerRoleCache.get(uuid).remove(target);
  }

  public void cachePlayerRoles(TownsTable townsTable) {
    if (townsTable.getPlayerRoles() == null) return;
    final String[] pairs = townsTable.getPlayerRoles().split(",");
    for (String pair : pairs) {
      final String[] parts = pair.split("\\+");
      if (parts.length == 2) {
        final String target = parts[0];
        final String role = parts[1];
        setPlayerRoleCache(townsTable.getUniqueId(), UUID.fromString(target), role);
      }
    }
  }

  public void deletePlayerRolesCache(UUID uuid) {
    playerRoleCache.remove(uuid);
  }

  public String getPlayerRole(UUID uuid, UUID target) {
    return playerRoleCache.get(uuid).get(target);
  }

  public void setPlayerRole(UUID uuid, UUID target, String role, boolean updateDatabase) {
    removePlayerRole(uuid, target, false);
    addPlayerRole(uuid, target, role, false);
    if (updateDatabase) cacheTowns.updateTownsDatabase(cacheTowns.getTownTable(uuid));
  }

  public void addPlayerRole(UUID uuid, UUID target, String role, boolean updateDatabase) {
    final TownsTable townsTable = cacheTowns.getTownTable(uuid);
    if (townsTable.getPlayerRoles() == null) townsTable.setPlayerRoles(target + "+" + role);
    else townsTable.setPlayerRoles(townsTable.getPlayerRoles() + "," + target + "+" + role);
    setPlayerRoleCache(uuid, target, role);
    if (updateDatabase) cacheTowns.updateTownsDatabase(townsTable);
  }

  public void removePlayerRole(UUID uuid, UUID target, boolean updateDatabase) {
    final TownsTable townsTable = cacheTowns.getTownTable(uuid);
    final Set<String> newRoles = Collections.synchronizedSet(new HashSet<>(List.of(townsTable.getPlayerRoles().split(","))));
    newRoles.remove(target + "+" + getPlayerRole(uuid, target));
    if (newRoles.isEmpty()) townsTable.setPlayerRoles(null);
    else townsTable.setPlayerRoles(StringUtils.join(newRoles, ","));
    removePlayerRoleCache(uuid, target);
    if (updateDatabase) cacheTowns.updateTownsDatabase(townsTable);
  }
}
