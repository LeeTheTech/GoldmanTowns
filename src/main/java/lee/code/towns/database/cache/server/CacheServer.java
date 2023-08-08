package lee.code.towns.database.cache.server;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.tables.ServerTable;
import lombok.Getter;

public class CacheServer extends DatabaseHandler {

    @Getter private ServerTable serverTable = null;

    public CacheServer(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public void setServerTable(ServerTable serverTable) {
        this.serverTable = serverTable;
    }

    public long getLastRentCollectionTime() {
        return serverTable.getRentCollectionTime();
    }

    public long getNextRentCollectionTime() {
        return Math.max(serverTable.getRentCollectionTime() - System.currentTimeMillis(), 0);
    }

    public void setLastRentCollectionTime(Long time) {
        serverTable.setRentCollectionTime(time);
        updateServerDatabase(serverTable);
    }

}
