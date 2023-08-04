package lee.code.towns.database;

import lee.code.towns.database.cache.chunks.CacheChunks;
import lee.code.towns.database.cache.handlers.FlagHandler;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;
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

    public boolean checkPlayerLocationFlag(UUID uuid, Location location, Flag flag, boolean ownerBypass) {
        final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
        if (!cacheChunks.isClaimed(chunk)) return false;
        final UUID owner = cacheChunks.getChunkOwner(chunk);
        if (ownerBypass && uuid.equals(owner)) return false;
        if (cacheTowns.isCitizen(owner, uuid)) {
            if (FlagHandler.isRoleFlag(flag)) {
                final String role = cacheTowns.getPlayerRoleData().getPlayerRole(owner, uuid);
                return !cacheTowns.getRoleData().checkRolePermissionFlag(owner, role, flag);
            }
        }
        if (cacheChunks.getChunkPermData().checkChunkPermissionFlag(chunk, Flag.CHUNK_FLAGS_ENABLED)) {
            return !cacheChunks.getChunkPermData().checkChunkPermissionFlag(chunk, flag);
        }
        return !cacheTowns.getPermData().checkGlobalPermissionFlag(owner, flag);
    }

    public boolean checkLocationFlag(Location location, Flag flag) {
        final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
        if (!cacheChunks.isClaimed(chunk)) return false;
        final UUID owner = cacheChunks.getChunkOwner(chunk);
        if (cacheChunks.getChunkPermData().checkChunkPermissionFlag(chunk, Flag.CHUNK_FLAGS_ENABLED)) {
            return !cacheChunks.getChunkPermData().checkChunkPermissionFlag(chunk, flag);
        }
        return !cacheTowns.getPermData().checkGlobalPermissionFlag(owner, flag);
    }

    public String getChunkTownName(Location location) {
        final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
        return cacheTowns.getTownName(cacheChunks.getChunkOwner(chunk));
    }

    public void deleteTown(UUID uuid) {
        final TownsTable townsTable = cacheTowns.getTownTable(uuid);
        for (UUID citizen : cacheTowns.getCitizensList(uuid)) {
            final TownsTable citizenTable = cacheTowns.getTownTable(citizen);
            citizenTable.setJoinedTown(null);
            cacheTowns.updateTownsDatabase(citizenTable);
        }
        townsTable.setTown(null);
        townsTable.setTownCitizens(null);
        townsTable.setPlayerRoles(null);
        cacheTowns.getPlayerRoleData().deleteAllPlayerRoles(uuid);
        cacheTowns.getRoleData().deleteAllRoles(uuid);
        cacheChunks.deleteAllChunkData(uuid);
    }
}
