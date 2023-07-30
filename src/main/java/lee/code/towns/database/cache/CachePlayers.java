package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.database.tables.PlayerTable;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Location;

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

    public boolean hasTown(UUID uuid) {
        return playersCache.get(uuid).getTown() != null;
    }

    public String getTown(UUID uuid) {
        return playersCache.get(uuid).getTown();
    }

    public void setTown(UUID uuid, String town, Location spawn) {
        final PlayerTable playerTable = playersCache.get(uuid);
        playerTable.setTown(town);
        playerTable.setSpawn(CoreUtil.serializeLocation(spawn));
        updatePlayerDatabase(playerTable);
    }

    public boolean hasJoinedTown(UUID uuid) {
        return playersCache.get(uuid).getJoinedTown() != null;
    }

    public String getJoinedTown(UUID uuid) {
        return playersCache.get(uuid).getJoinedTown();
    }

    public boolean isTownTaken(String name) {
        return playersCache.values().stream()
                .anyMatch(playerData -> playerData.getTown() != null && playerData.getTown().equals(name));
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
