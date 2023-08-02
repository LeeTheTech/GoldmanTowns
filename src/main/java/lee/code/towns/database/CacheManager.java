package lee.code.towns.database;

import lee.code.towns.database.cache.CacheChunks;
import lee.code.towns.database.cache.CacheTowns;
import lee.code.towns.enums.Flag;
import lee.code.towns.utils.ChunkUtil;
import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

public class CacheManager {
    @Getter private final CacheChunks cacheChunks;
    @Getter private final CacheTowns cacheTowns;

    public CacheManager(DatabaseManager databaseManager) {
        this.cacheChunks = new CacheChunks(databaseManager);
        this.cacheTowns = new CacheTowns(databaseManager);
    }

    public boolean checkPlayerLocationFlag(UUID uuid, Location location, Flag flag) {
        final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
        if (!cacheChunks.isClaimed(chunk)) return false;
        final UUID owner = cacheChunks.getChunkOwner(chunk);
        if (uuid.equals(owner)) return false;
        if (cacheTowns.isCitizen(owner, uuid)) {
            final String role = cacheTowns.getPlayerRole(owner, uuid);
            return !cacheTowns.checkRolePermissionFlag(owner, role, flag);
        }
        if (cacheChunks.checkChunkPermissionFlag(chunk, Flag.CHUNK_FLAGS_ENABLED)) {
            return !cacheChunks.checkChunkPermissionFlag(chunk, flag);
        }
        return !cacheTowns.checkGlobalPermissionFlag(owner, flag);
    }

    public boolean checkLocationFlag(Location location, Flag flag) {
        final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
        if (!cacheChunks.isClaimed(chunk)) return false;
        final UUID owner = cacheChunks.getChunkOwner(chunk);
        if (cacheChunks.checkChunkPermissionFlag(chunk, Flag.CHUNK_FLAGS_ENABLED)) {
            return !cacheChunks.checkChunkPermissionFlag(chunk, flag);
        }
        return !cacheTowns.checkGlobalPermissionFlag(owner, flag);
    }

    public String getChunkTownName(Location location) {
        final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
        return cacheTowns.getTown(cacheChunks.getChunkOwner(chunk));
    }
}
