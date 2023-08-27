package lee.code.towns.database.cache.chunks.data;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.utils.FlagUtil;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;

import java.util.concurrent.ConcurrentHashMap;

public class ChunkPermData extends DatabaseHandler {
  private final ConcurrentHashMap<String, PermissionTable> permissionCache = new ConcurrentHashMap<>();

  public ChunkPermData(DatabaseManager databaseManager) {
    super(databaseManager);
  }

  public PermissionTable getPermissionTable(String chunk) {
    return permissionCache.get(chunk);
  }

  public void removePermissionTable(String chunk) {
    permissionCache.remove(chunk);
  }

  public void setPermissionTable(PermissionTable permissionTable) {
    permissionCache.put(permissionTable.getChunk(), permissionTable);
  }

  public boolean checkChunkPermissionFlag(String chunk, Flag flag) {
    final PermissionTable permissionTable = permissionCache.get(chunk);
    return FlagUtil.checkPermissionFlag(permissionTable, flag);
  }

  public void setChunkPermissionFlag(String chunk, Flag flag, boolean result) {
    final PermissionTable permissionTable = permissionCache.get(chunk);
    FlagUtil.setPermissionFlag(permissionTable, flag, result);
    updatePermissionDatabase(permissionTable);
  }
}
