package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.utils.FlagUtil;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.enums.TownRole;
import lee.code.towns.utils.CoreUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TownRoleData extends DatabaseHandler {
  private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, PermissionTable>> rolePermissionCache = new ConcurrentHashMap<>();

  public TownRoleData(DatabaseManager databaseManager) {
    super(databaseManager);
  }

  private PermissionTable getRolePermissionTable(UUID uuid, String role) {
    return rolePermissionCache.get(uuid).get(role);
  }

  public void setRolePermissionFlag(UUID uuid, String role, Flag flag, boolean result) {
    PermissionTable permissionTable = getRolePermissionTable(uuid, role);
    FlagUtil.setPermissionFlag(permissionTable, flag, result);
    updatePermissionDatabase(permissionTable);
  }

  public boolean checkRolePermissionFlag(UUID uuid, String role, Flag flag) {
    PermissionTable permissionTable = getRolePermissionTable(uuid, role);
    return FlagUtil.checkPermissionFlag(permissionTable, flag);
  }

  public void createDefaultRolePermissionTable(UUID uuid) {
    PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.ROLE);
    permissionTable.setRole(CoreUtil.capitalize(TownRole.CITIZEN.name()));
    setRolePermissionTable(permissionTable);
    createPermissionDatabase(permissionTable);
  }

  public void setRolePermissionTable(PermissionTable permissionTable) {
    if (rolePermissionCache.containsKey(permissionTable.getUniqueID())) {
      rolePermissionCache.get(permissionTable.getUniqueID()).put(permissionTable.getRole(), permissionTable);
    } else {
      ConcurrentHashMap<String, PermissionTable> newRolePermTable = new ConcurrentHashMap<>();
      newRolePermTable.put(permissionTable.getRole(), permissionTable);
      rolePermissionCache.put(permissionTable.getUniqueID(), newRolePermTable);
    }
  }

  public void setRolePermissionTable(List<PermissionTable> permissionTables) {
    for (PermissionTable permissionTable : permissionTables) setRolePermissionTable(permissionTable);
  }

  public void createRolePermissionData(UUID uuid, String role) {
    PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.ROLE);
    permissionTable.setRole(role);
    setRolePermissionTable(permissionTable);
    createPermissionDatabase(permissionTable);
  }

  public void deleteRole(UUID uuid, String role) {
    deletePermissionDatabase(rolePermissionCache.get(uuid).get(role));
    rolePermissionCache.get(uuid).remove(role);
  }

  public List<String> getAllRoles(UUID uuid) {
    return new ArrayList<>(Collections.list(rolePermissionCache.get(uuid).keys()));
  }

  public List<String> getAllRolesAndMayor(UUID uuid) {
    List<String> roles = new ArrayList<>(Collections.list(rolePermissionCache.get(uuid).keys()));
    roles.add(CoreUtil.capitalize(TownRole.MAYOR.name()));
    return roles;
  }

  public void deleteAllRoleData(UUID uuid) {
    deleteAllRolePermissionDatabase(uuid);
    rolePermissionCache.remove(uuid);
  }
}
