package lee.code.towns.database.cache.handlers;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.*;

import java.util.UUID;

public class DatabaseHandler {
    private final DatabaseManager databaseManager;

    public DatabaseHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void deletePermissionDatabase(PermissionTable permissionTable) {
        databaseManager.deletePermissionTable(permissionTable);
    }

    public void createPermissionDatabase(PermissionTable permissionTable) {
        databaseManager.createPermissionTable(permissionTable);
    }

    public void updatePermissionDatabase(PermissionTable permissionTable) {
        databaseManager.updatePermissionTable(permissionTable);
    }

    public void createTownsDatabase(TownsTable townsTable) {
        databaseManager.createTownsTable(townsTable);
    }

    public void updateTownsDatabase(TownsTable townsTable) {
        databaseManager.updateTownsTable(townsTable);
    }

    public void updateChunkDatabase(ChunkTable chunkTable) {
        databaseManager.updateChunkTable(chunkTable);
    }

    public void createChunkDatabase(ChunkTable chunkTable) {
        databaseManager.createChunkTable(chunkTable);
    }
    public void deleteChunkDatabase(ChunkTable chunkTable) {
        databaseManager.deleteChunkTable(chunkTable);
    }

    public void deleteAllChunkDatabase(UUID uuid) {
        databaseManager.deleteAllChunkTables(uuid);
    }

    public void createRentDatabase(RentTable rentTable) {
        databaseManager.createRentTable(rentTable);
    }

    public void updateRentDatabase(RentTable rentTable) {
        databaseManager.updateRentTable(rentTable);
    }

    public void deleteRentDatabase(RentTable rentTable) {
        databaseManager.deleteRentTable(rentTable);
    }

    public void updateServerDatabase(ServerTable serverTable) {
        databaseManager.updateServerTable(serverTable);
    }
}
