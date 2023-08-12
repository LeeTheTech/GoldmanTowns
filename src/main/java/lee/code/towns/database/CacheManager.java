package lee.code.towns.database;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.chunks.CacheChunks;
import lee.code.towns.utils.CoreUtil;
import lee.code.towns.utils.FlagUtil;
import lee.code.towns.database.cache.renters.CacheRenters;
import lee.code.towns.database.cache.server.CacheServer;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;
import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheManager {
    private final Towns towns;
    @Getter private final CacheChunks cacheChunks;
    @Getter private final CacheTowns cacheTowns;
    @Getter private final CacheRenters cacheRenters;
    @Getter private final CacheServer cacheServer;

    public CacheManager(Towns towns, DatabaseManager databaseManager) {
        this.towns = towns;
        this.cacheChunks = new CacheChunks(databaseManager);
        this.cacheTowns = new CacheTowns(databaseManager);
        this.cacheRenters = new CacheRenters(databaseManager);
        this.cacheServer = new CacheServer(databaseManager);
    }

    public boolean checkPlayerLocationFlag(UUID uuid, Location location, Flag flag, boolean ownerBypass) {
        final String chunk = ChunkUtil.serializeChunkLocation(location.getChunk());
        if (!cacheChunks.isClaimed(chunk)) return false;
        if (cacheRenters.isRented(chunk)) {
            if (cacheRenters.isPlayerRenting(uuid, chunk)) return false;
            if (cacheTowns.getTrustData().isTrusted(cacheRenters.getRenter(chunk), uuid)) return false;
            if (cacheChunks.getChunkPermData().checkChunkPermissionFlag(chunk, Flag.CHUNK_FLAGS_ENABLED)) {
                return !cacheChunks.getChunkPermData().checkChunkPermissionFlag(chunk, flag);
            }
            return true;
        }
        final UUID owner = cacheChunks.getChunkOwner(chunk);
        if (ownerBypass && uuid.equals(owner)) return false;
        if (cacheTowns.isCitizen(owner, uuid)) {
            if (FlagUtil.isRoleFlag(flag)) {
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

    public String getChunkTownName(String chunk) {
        return cacheTowns.getTownName(cacheChunks.getChunkOwner(chunk));
    }

    public void createTown(UUID uuid, String town, Location spawn) {
        cacheChunks.claimEstablishedChunk(ChunkUtil.serializeChunkLocation(spawn.getChunk()), uuid);
        final TownsTable townsTable = cacheTowns.getTownTable(uuid);
        townsTable.setTown(town);
        townsTable.setSpawn(CoreUtil.serializeLocation(spawn));
        cacheTowns.getRoleColorData().setDefaultRoleColor(uuid, false);
        cacheTowns.updateTownsDatabase(townsTable);
        cacheTowns.getRoleData().createDefaultRolePermissionTable(uuid);
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
        townsTable.setSpawn(null);
        townsTable.setRoleColors(null);
        cacheTowns.getRoleColorData().deleteAllRoleColors(uuid);
        cacheTowns.getPlayerRoleData().deleteAllPlayerRoles(uuid);
        cacheTowns.getRoleData().deleteAllRoles(uuid);
        cacheChunks.deleteAllChunkData(uuid);
        cacheTowns.updateTownsDatabase(townsTable);
    }

    public void startRentCollectionTask() {
        Bukkit.getAsyncScheduler().runAtFixedRate(towns, (scheduledTask) -> {
            if (cacheServer.getLastRentCollectionTime() <= System.currentTimeMillis()) {
                //TODO take that money from all players renting
                Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.RENT_COLLECTION_FINISHED.getComponent(null)));
                cacheServer.setLastRentCollectionTime(System.currentTimeMillis() + 86400000L);
            }
                },
                0,
                1,
                TimeUnit.MINUTES
        );
    }
}
