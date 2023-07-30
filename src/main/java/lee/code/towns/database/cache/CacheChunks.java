package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.ChunkTable;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.PermissionType;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheChunks {

    private final DatabaseManager databaseManager;
    private final ConcurrentHashMap<String, ChunkTable> chunksCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, HashSet<String>> playerChunkListCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PermissionTable> permissionCache = new ConcurrentHashMap<>();

    public CacheChunks(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //Player Chunk List Data
    private void addToPlayerChunkListCache(UUID uuid, String chunk) {
        if (playerChunkListCache.containsKey(uuid)) {
            playerChunkListCache.get(uuid).add(chunk);
        } else {
            final HashSet<String> chunks = new HashSet<>();
            chunks.add(chunk);
            playerChunkListCache.put(uuid, chunks);
        }
    }

    private void removeFromPlayerChunkListCache(UUID uuid, String chunk) {
        playerChunkListCache.get(uuid).remove(chunk);
    }

    public HashSet<String> getChunkList(UUID uuid) {
        return playerChunkListCache.get(uuid);
    }

    //Chunk Data
    private void updateChunkDatabase(ChunkTable chunkTable) {
        databaseManager.updateChunkTable(chunkTable);
    }

    private void createChunkDatabase(ChunkTable chunkTable) {
        databaseManager.createChunkTable(chunkTable);
    }

    public void setChunkTable(ChunkTable chunkTable) {
        chunksCache.put(chunkTable.getChunk(), chunkTable);
        addToPlayerChunkListCache(chunkTable.getOwner(), chunkTable.getChunk());
    }

    public boolean isClaimed(String chunk) {
        return chunksCache.containsKey(chunk);
    }

    public void claim(String chunk, UUID uuid) {
        final ChunkTable chunkTable = new ChunkTable(chunk, uuid);
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.CHUNK);
        permissionTable.setChunk(chunk);
        setChunkTable(chunkTable);
        createChunkDatabase(chunkTable);
        setPermissionTable(permissionTable);
        createPermissionDatabase(permissionTable);
        addToPlayerChunkListCache(uuid, chunk);
    }

    public UUID getChunkOwner(String chunk) {
        return chunksCache.get(chunk).getOwner();
    }


    //Permission Data
    private void createPermissionDatabase(PermissionTable permissionTable) {
        databaseManager.createPermissionTable(permissionTable);
    }

    public void updatePermissionDatabase(PermissionTable permissionTable) {
        databaseManager.updatePermissionTable(permissionTable);
    }

    public void setPermissionTable(PermissionTable permissionTable) {
        permissionCache.put(permissionTable.getChunk(), permissionTable);
    }
}
