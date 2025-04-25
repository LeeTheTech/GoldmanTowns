package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;
import lee.code.towns.enums.TownRole;
import lee.code.towns.utils.CoreUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TownRoleColorData {
  private final CacheTowns cacheTowns;
  private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, String>> roleColorCache = new ConcurrentHashMap<>();

  public TownRoleColorData(CacheTowns cacheTowns) {
    this.cacheTowns = cacheTowns;
  }

  private void setRoleColorCache(UUID uuid, String role, String color) {
    if (roleColorCache.containsKey(uuid)) {
      roleColorCache.get(uuid).put(role, color);
    } else {
      ConcurrentHashMap<String, String> roles = new ConcurrentHashMap<>();
      roles.put(role, color);
      roleColorCache.put(uuid, roles);
    }
  }

  private void removeRoleColorCache(UUID uuid, String role) {
    roleColorCache.get(uuid).remove(role);
  }

  public void cacheRoleColors(TownsTable townsTable) {
    if (townsTable.getRoleColors() == null) return;
    String[] pairs = townsTable.getRoleColors().split(",");
    for (String pair : pairs) {
      String[] parts = pair.split("\\+");
      if (parts.length == 2) {
        String role = parts[0];
        String color = parts[1];
        setRoleColorCache(townsTable.getUniqueId(), role, color);
      }
    }
  }

  public void deleteRoleColorCache(UUID uuid) {
    roleColorCache.remove(uuid);
  }

  public String getRoleColor(UUID uuid, String role) {
    return roleColorCache.get(uuid).get(role);
  }

  public String getRoleWithColor(UUID uuid, String role) {
    return roleColorCache.get(uuid).get(role) + role;
  }

  public void setRoleColor(UUID uuid, String role, String color, boolean updateDatabase) {
    removeRoleColor(uuid, role, false);
    addRoleColor(uuid, role, color, false);
    if (updateDatabase) cacheTowns.updateTownsDatabase(cacheTowns.getTownTable(uuid));
  }

  public void addRoleColor(UUID uuid, String role, String color, boolean updateDatabase) {
    TownsTable townsTable = cacheTowns.getTownTable(uuid);
    if (townsTable.getRoleColors() == null) townsTable.setRoleColors(role + "+" + color);
    else townsTable.setRoleColors(townsTable.getRoleColors() + "," + role + "+" + color);
    setRoleColorCache(uuid, role, color);
    if (updateDatabase) cacheTowns.updateTownsDatabase(townsTable);
  }

  public void removeRoleColor(UUID uuid, String role, boolean updateDatabase) {
    TownsTable townsTable = cacheTowns.getTownTable(uuid);
    Set<String> roleColors = Collections.synchronizedSet(new HashSet<>(List.of(townsTable.getRoleColors().split(","))));
    roleColors.remove(role + "+" + getRoleColor(uuid, role));
    if (roleColors.isEmpty()) townsTable.setRoleColors(null);
    else townsTable.setRoleColors(StringUtils.join(roleColors, ","));
    removeRoleColorCache(uuid, role);
    if (updateDatabase) cacheTowns.updateTownsDatabase(townsTable);
  }

  public void setDefaultRoleColor(UUID uuid, boolean updateDatabase) {
    addRoleColor(uuid, CoreUtil.capitalize(TownRole.CITIZEN.name()), "&b", updateDatabase);
    addRoleColor(uuid, CoreUtil.capitalize(TownRole.MAYOR.name()), "&c", updateDatabase);
  }
}
