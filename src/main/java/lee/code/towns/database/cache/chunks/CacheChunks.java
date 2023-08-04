package lee.code.towns.database.cache.chunks;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.chunks.data.ChunkPermData;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.cache.chunks.data.ChunkPlayerListData;
import lee.code.towns.database.tables.ChunkTable;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.PermissionType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheChunks extends DatabaseHandler {

    @Getter private final ChunkPlayerListData chunkPlayerListData;
    @Getter private final ChunkPermData chunkPermData;
    private final ConcurrentHashMap<String, ChunkTable> chunksCache = new ConcurrentHashMap<>();

    public CacheChunks(DatabaseManager databaseManager) {
        super(databaseManager);
        this.chunkPlayerListData = new ChunkPlayerListData();
        this.chunkPermData = new ChunkPermData(databaseManager);
    }

    private void deleteChunkData(String chunk) {
        deleteChunkDatabase(chunksCache.get(chunk));
        deletePermissionDatabase(chunkPermData.getPermissionTable(chunk));
        chunkPlayerListData.removeChunkList(getChunkOwner(chunk), chunk);
        chunksCache.remove(chunk);
        chunkPermData.removePermissionTable(chunk);
    }

    private void createChunkData(String chunk, UUID uuid) {
        final ChunkTable chunkTable = new ChunkTable(chunk, uuid);
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.CHUNK);
        permissionTable.setChunk(chunk);
        setChunkTable(chunkTable);
        createChunkDatabase(chunkTable);
        chunkPermData.setPermissionTable(permissionTable);
        chunkPlayerListData.addChunkList(uuid, chunk);
        createPermissionDatabase(permissionTable);
    }

    public void deleteAllChunkData(UUID uuid) {
        final Set<String> chunks = getChunkList(uuid);
        chunks.forEach(chunksCache::remove);
        chunks.forEach(chunkPermData::removePermissionTable);
        chunkPlayerListData.removeAllChunkList(uuid);
        deleteAllChunkDatabase(uuid);
    }

    public Set<String> getChunkList(UUID uuid) {
        return chunkPlayerListData.getChunkList(uuid);
    }

    public boolean hasClaimedChunks(UUID uuid) {
        return chunkPlayerListData.hasClaimedChunks(uuid);
    }

    public int getChunkClaims(UUID uuid) {
        return chunkPlayerListData.getChunkList(uuid).size();
    }

    public void setChunkTable(ChunkTable chunkTable) {
        chunksCache.put(chunkTable.getChunk(), chunkTable);
        chunkPlayerListData.addChunkList(chunkTable.getOwner(), chunkTable.getChunk());
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

    public UUID getChunkOwner(String chunk) {
        return chunksCache.get(chunk).getOwner();
    }

    public boolean isChunkOwner(String chunk, UUID uuid) {
        return chunksCache.get(chunk).getOwner().equals(uuid);
    }

    public void claimChunk(String chunk, UUID uuid) {
        createChunkData(chunk, uuid);
    }

    public void unclaimChunk(String chunk) {
        deleteChunkData(chunk);
    }
}
