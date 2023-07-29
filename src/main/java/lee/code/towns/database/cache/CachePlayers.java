package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.database.tables.PermissionType;
import lee.code.towns.database.tables.PlayerTable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachePlayers {

    private final DatabaseManager databaseManager;
    private final ConcurrentHashMap<UUID, PlayerTable> playersCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, PermissionTable> permissionCache = new ConcurrentHashMap<>();

    public CachePlayers(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //Player Data
    private void createPlayerDatabase(PlayerTable playerTable) {
        databaseManager.createPlayerTable(playerTable);
    }

    private void updatePlayerDatabase(PlayerTable playerTable) {
        databaseManager.updatePlayerTable(playerTable);
    }

    public void createPlayerData(UUID uuid) {
        final PlayerTable playerTable = new PlayerTable(uuid);
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.PLAYER);
        setPlayerTable(playerTable);
        setPermissionTable(permissionTable);
        createPlayerDatabase(playerTable);
        createPermissionDatabase(permissionTable);
    }

    public boolean hasPlayerData(UUID uuid) {
        return playersCache.containsKey(uuid);
    }

    public void setPlayerTable(PlayerTable playerTable) {
        playersCache.put(playerTable.getUniqueID(), playerTable);
    }

    //Permission Data
    private void createPermissionDatabase(PermissionTable permissionTable) {
        databaseManager.createPermissionTable(permissionTable);
    }

    private void updatePermissionDatabase(PermissionTable permissionTable) {
        databaseManager.updatePermissionTable(permissionTable);
    }

    public void setPermissionTable(PermissionTable permissionTable) {
        permissionCache.put(permissionTable.getUniqueID(), permissionTable);
    }

}
