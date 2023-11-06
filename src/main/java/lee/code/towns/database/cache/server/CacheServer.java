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

  public long getLastCollectionTime() {
    return serverTable.getCollectionTime();
  }

  public long getNextCollectionTime() {
    return Math.max(serverTable.getCollectionTime() - System.currentTimeMillis(), 0);
  }

  public void setLastCollectionTime(long time) {
    serverTable.setCollectionTime(time);
    updateServerDatabase(serverTable);
  }
}
