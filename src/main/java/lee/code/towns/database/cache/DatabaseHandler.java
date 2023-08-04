package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.database.tables.TownsTable;

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
}
