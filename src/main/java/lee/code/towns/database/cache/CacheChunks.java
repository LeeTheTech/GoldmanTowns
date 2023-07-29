package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.ChunkTable;
import lee.code.towns.database.tables.PermissionTable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheChunks {

    private final DatabaseManager databaseManager;
    private final ConcurrentHashMap<String, ChunkTable> chunksCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PermissionTable> permissionCache = new ConcurrentHashMap<>();

    public CacheChunks(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //Chunk Data
    private void updateChunkDatabase(ChunkTable chunkTable) {
        databaseManager.updateChunkTable(chunkTable);
    }

    public void setChunkTable(ChunkTable chunkTable) {
        chunksCache.put(chunkTable.getChunk(), chunkTable);
    }

    public boolean isClaimed(String chunk) {
        return chunksCache.containsKey(chunk);
    }

    public UUID getChunkOwner(String chunk) {
        return chunksCache.get(chunk).getOwner();
    }


    //Permission Data
    public void updatePermissionDatabase(PermissionTable permissionTable) {
        databaseManager.updatePermissionTable(permissionTable);
    }

    public void setPermissionTable(PermissionTable permissionTable) {
        permissionCache.put(permissionTable.getChunk(), permissionTable);
    }
}
