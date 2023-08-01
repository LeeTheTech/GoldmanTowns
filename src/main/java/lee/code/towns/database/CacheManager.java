package lee.code.towns.database;

import lee.code.towns.database.cache.CacheChunks;
import lee.code.towns.database.cache.CacheTowns;
import lombok.Getter;

public class CacheManager {
    @Getter private final CacheChunks cacheChunks;
    @Getter private final CacheTowns cacheTowns;

    public CacheManager(DatabaseManager databaseManager) {
        this.cacheChunks = new CacheChunks(databaseManager);
        this.cacheTowns = new CacheTowns(databaseManager);
    }
}
