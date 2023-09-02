package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.utils.FlagUtil;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TownPermData extends DatabaseHandler {
  private final ConcurrentHashMap<UUID, PermissionTable> permissionCache = new ConcurrentHashMap<>();

  public TownPermData(DatabaseManager databaseManager) {
    super(databaseManager);
  }

  private PermissionTable getPermissionTable(UUID uuid) {
    return permissionCache.get(uuid);
  }

  public void setPermissionTable(PermissionTable permissionTable) {
    permissionCache.put(permissionTable.getUniqueID(), permissionTable);
  }

  public void setPermissionTable(List<PermissionTable> permissionTables) {
    for (PermissionTable permissionTable : permissionTables) permissionCache.put(permissionTable.getUniqueID(), permissionTable);
  }

  public void setGlobalPermissionFlag(UUID uuid, Flag flag, boolean result) {
    final PermissionTable permissionTable = getPermissionTable(uuid);
    FlagUtil.setPermissionFlag(permissionTable, flag, result);
    updatePermissionDatabase(permissionTable);
  }

  public boolean checkGlobalPermissionFlag(UUID uuid, Flag flag) {
    final PermissionTable permissionTable = getPermissionTable(uuid);
    return FlagUtil.checkPermissionFlag(permissionTable, flag);
  }

  public void createDefaultTownPermissionTable(UUID uuid) {
    final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.TOWN);
    setPermissionTable(permissionTable);
    createPermissionDatabase(permissionTable);
  }
}
