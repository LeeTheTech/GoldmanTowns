package lee.code.towns.database.cache.chunks;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.chunks.data.ChunkOutpostData;
import lee.code.towns.database.cache.chunks.data.ChunkPermData;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.cache.chunks.data.ChunkListData;
import lee.code.towns.database.tables.ChunkTable;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.utils.ChunkGraphUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheChunks extends DatabaseHandler {

    @Getter private final ChunkListData chunkListData;
    @Getter private final ChunkOutpostData chunkOutpostData;
    @Getter private final ChunkPermData chunkPermData;
    private final ConcurrentHashMap<String, ChunkTable> chunksCache = new ConcurrentHashMap<>();

    public CacheChunks(DatabaseManager databaseManager) {
        super(databaseManager);
        this.chunkListData = new ChunkListData();
        this.chunkOutpostData = new ChunkOutpostData();
        this.chunkPermData = new ChunkPermData(databaseManager);
    }

    private void deleteChunkData(String chunk) {
        final ChunkTable chunkTable = chunksCache.get(chunk);
        deleteChunkDatabase(chunkTable);
        deletePermissionDatabase(chunkPermData.getPermissionTable(chunk));
        if (chunkTable.isOutpost()) chunkOutpostData.removeChunkList(chunkTable.getOwner(), chunk);
        chunkListData.removeChunkList(chunkTable.getOwner(), chunk);
        chunksCache.remove(chunk);
        chunkPermData.removePermissionTable(chunk);
    }

    private void createChunkData(String chunk, UUID uuid, boolean outpost, boolean townChunk) {
        final ChunkTable chunkTable = new ChunkTable(chunk, uuid);
        chunkTable.setOutpost(outpost);
        chunkTable.setEstablishedChunk(townChunk);
        setChunkTable(chunkTable);
        createChunkDatabase(chunkTable);
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.CHUNK);
        permissionTable.setChunk(chunk);
        chunkPermData.setPermissionTable(permissionTable);
        createPermissionDatabase(permissionTable);
    }

    public void deleteAllChunkData(UUID uuid) {
        final Set<String> chunks = chunkListData.getChunkList(uuid);
        chunks.forEach(chunksCache::remove);
        chunks.forEach(chunkPermData::removePermissionTable);
        chunkListData.removeAllChunkList(uuid);
        chunkOutpostData.removeAllChunkList(uuid);
        deleteAllChunkDatabase(uuid);
    }

    public int getChunkClaims(UUID uuid) {
        return chunkListData.getChunkList(uuid).size();
    }

    public void setChunkTable(ChunkTable chunkTable) {
        chunksCache.put(chunkTable.getChunk(), chunkTable);
        chunkListData.addChunkList(chunkTable.getOwner(), chunkTable.getChunk());
        if (chunkTable.isOutpost()) chunkOutpostData.addChunkList(chunkTable.getOwner(), chunkTable.getChunk());
    }

    public boolean isClaimed(String chunk) {
        return chunksCache.containsKey(chunk);
    }

    public boolean isConnectedChunk(UUID uuid, String chunk) {
        final Set<String> outposts = chunkOutpostData.getChunkList(uuid);
        final String[] chunkParts = StringUtils.split(chunk, ",");
        final int[] offset = {-1, 0, 1};
        final String world = chunkParts[0];
        final int baseX = Integer.parseInt(chunkParts[1]);
        final int baseZ = Integer.parseInt(chunkParts[2]);

        for (int x : offset) {
            for (int z : offset) {
                if (x == 0 && z == 0) continue;
                final String sChunk = world + "," + (baseX + x) + "," + (baseZ + z);
                if (outposts.contains(sChunk)) continue;
                if (isClaimed(sChunk)) {
                    if (isChunkOwner(sChunk, uuid)) return true;
                }
            }
        }
        return false;
    }

    public boolean isUnclaimSafe(UUID uuid, String chunk) {
        return ChunkGraphUtil.areChunksConnected(chunkListData.getChunkList(uuid), chunkOutpostData.getChunkList(uuid), chunk);
    }

    public boolean isEstablishedChunk(String chunk) {
        return chunksCache.get(chunk).isEstablishedChunk();
    }

    public UUID getChunkOwner(String chunk) {
        return chunksCache.get(chunk).getOwner();
    }

    public boolean isChunkOwner(String chunk, UUID uuid) {
        return chunksCache.get(chunk).getOwner().equals(uuid);
    }

    public void claimEstablishedChunk(String chunk, UUID uuid) {
        createChunkData(chunk, uuid, false, true);
    }

    public void claimChunk(String chunk, UUID uuid) {
        createChunkData(chunk, uuid, false, false);
    }

    public void claimOutpost(String chunk, UUID uuid) {
        createChunkData(chunk, uuid, true, false);
    }

    public void unclaimChunk(String chunk) {
        deleteChunkData(chunk);
    }
}
