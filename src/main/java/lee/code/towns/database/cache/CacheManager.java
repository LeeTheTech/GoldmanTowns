package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lombok.Getter;

public class CacheManager {
    @Getter private final CacheChunks cacheChunks;
    @Getter private final CachePlayers cachePlayers;

    public CacheManager(DatabaseManager databaseManager) {
        this.cacheChunks = new CacheChunks(databaseManager);
        this.cachePlayers = new CachePlayers(databaseManager);
    }
}
