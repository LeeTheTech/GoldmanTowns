package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.ChunkTable;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.utils.PermissionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheChunks {

    private final DatabaseManager databaseManager;
    private final ConcurrentHashMap<String, ChunkTable> chunksCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Set<String>> playerChunkListCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PermissionTable> permissionCache = new ConcurrentHashMap<>();

    public CacheChunks(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //Player Chunk List Data
    private void addToPlayerChunkListCache(UUID uuid, String chunk) {
        if (playerChunkListCache.containsKey(uuid)) {
            playerChunkListCache.get(uuid).add(chunk);
        } else {
            final Set<String> chunks = Collections.synchronizedSet(new HashSet<>());
            chunks.add(chunk);
            playerChunkListCache.put(uuid, chunks);
        }
    }

    private void removeFromPlayerChunkListCache(UUID uuid, String chunk) {
        playerChunkListCache.get(uuid).remove(chunk);
    }

    public Set<String> getChunkList(UUID uuid) {
        return playerChunkListCache.get(uuid);
    }

    public boolean hasClaimedChunks(UUID uuid) {
        return playerChunkListCache.containsKey(uuid);
    }

    //Chunk Data
    private void updateChunkDatabase(ChunkTable chunkTable) {
        databaseManager.updateChunkTable(chunkTable);
    }

    private void createChunkDatabase(ChunkTable chunkTable) {
        databaseManager.createChunkTable(chunkTable);
    }

    private void deleteChunkData(String chunk) {
        databaseManager.deleteChunkTable(chunksCache.get(chunk));
        databaseManager.deletePermissionTable(permissionCache.get(chunk));
        playerChunkListCache.get(getChunkOwner(chunk)).remove(chunk);
        chunksCache.remove(chunk);
        permissionCache.remove(chunk);
    }

    public void setChunkTable(ChunkTable chunkTable) {
        chunksCache.put(chunkTable.getChunk(), chunkTable);
        addToPlayerChunkListCache(chunkTable.getOwner(), chunkTable.getChunk());
    }

    public boolean isClaimed(String chunk) {
        return chunksCache.containsKey(chunk);
    }

    public boolean isConnectedChunk(UUID uuid, String chunk) {
        final String[] chunkParts = StringUtils.split(chunk, ",");
        final int[] offset = {-1, 0, 1};
        final String world = chunkParts[0];
        final int baseX = Integer.parseInt(chunkParts[1]);
        final int baseZ = Integer.parseInt(chunkParts[2]);

        for (int x : offset) {
            for (int z : offset) {
                final String sChunk = world + "," + (baseX + x) + "," + (baseZ + z);
                if (isClaimed(sChunk)) {
                    if (isChunkOwner(sChunk, uuid)) return true;
                }
            }
        }
        return false;
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

    public boolean isChunkOwner(String chunk, UUID uuid) {
        return chunksCache.get(chunk).getOwner().equals(uuid);
    }

    public void unclaimChunk(String chunk) {
        deleteChunkData(chunk);
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

    public boolean checkChunkPermissionFlag(String chunk, Flag flag) {
        final PermissionTable permissionTable = permissionCache.get(chunk);
        return PermissionUtil.checkPermissionFlag(permissionTable, flag);
    }

    public void setChunkPermissionFlag(String chunk, Flag flag, boolean result) {
        final PermissionTable permissionTable = permissionCache.get(chunk);
        PermissionUtil.setPermissionFlag(permissionTable, flag, result);
        updatePermissionDatabase(permissionTable);
    }
}
