package lee.code.towns.database;

import lee.code.economy.EcoAPI;
import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.Towns;
import lee.code.towns.database.cache.bank.CacheBank;
import lee.code.towns.database.cache.chunks.CacheChunks;
import lee.code.towns.enums.GlobalValue;
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
  @Getter private final CacheBank cacheBank;

  public CacheManager(Towns towns, DatabaseManager databaseManager) {
    this.towns = towns;
    this.cacheChunks = new CacheChunks(databaseManager);
    this.cacheTowns = new CacheTowns(databaseManager);
    this.cacheRenters = new CacheRenters(databaseManager);
    this.cacheServer = new CacheServer(databaseManager);
    this.cacheBank = new CacheBank(databaseManager);
  }

  public boolean checkPlayerLocationFlag(UUID uuid, String chunk, Flag flag, boolean ownerBypass) {
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
    if (cacheTowns.getCitizenData().isCitizen(owner, uuid)) {
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

  public boolean checkLocationFlag(String chunk, Flag flag) {
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

  public String getChunkTownOwnerName(String chunk) {
    return PlayerDataAPI.getName(cacheChunks.getChunkOwner(chunk));
  }

  public void createTown(UUID uuid, String town, Location spawn) {
    cacheChunks.claimEstablishedChunk(ChunkUtil.serializeChunkLocation(spawn.getChunk()), uuid);
    final TownsTable townsTable = cacheTowns.getTownTable(uuid);
    cacheTowns.getTownNameListData().setTownName(town, uuid, false);
    townsTable.setSpawn(CoreUtil.serializeLocation(spawn));
    cacheTowns.getRoleColorData().setDefaultRoleColor(uuid, false);
    cacheTowns.updateTownsDatabase(townsTable);
    cacheTowns.getPermData().createDefaultTownPermissionTable(uuid);
    cacheTowns.getRoleData().createDefaultRolePermissionTable(uuid);
    cacheBank.createBankData(uuid);
  }

  public void deleteTown(UUID uuid) {
    final TownsTable townsTable = cacheTowns.getTownTable(uuid);
    for (UUID citizen : cacheTowns.getCitizenData().getCitizensList(uuid)) {
      final TownsTable citizenTable = cacheTowns.getTownTable(citizen);
      citizenTable.setJoinedTown(null);
      cacheTowns.getTrustData().removeAllTrustedData(citizen);
      cacheTowns.updateTownsDatabase(citizenTable);
    }
    cacheTowns.getTownNameListData().removeTownName(townsTable.getTown());
    townsTable.setTown(null);
    townsTable.setTownCitizens(null);
    townsTable.setPlayerRoles(null);
    townsTable.setSpawn(null);
    townsTable.setRoleColors(null);
    townsTable.setBanner(null);
    cacheTowns.getCitizenData().deleteCitizenCache(uuid);
    cacheTowns.getRoleColorData().deleteRoleColorCache(uuid);
    cacheTowns.getPlayerRoleData().deletePlayerRolesCache(uuid);
    cacheTowns.getRoleData().deleteAllRoleData(uuid);
    cacheTowns.getPermData().deleteTownPermissionTable(uuid);
    cacheRenters.deleteAllRentData(uuid);
    cacheChunks.deleteAllChunkData(uuid);
    cacheBank.deleteAllBankData(uuid);
    cacheTowns.updateTownsDatabase(townsTable);
  }

  public void removeFromTown(UUID uuid) {
    cacheRenters.deleteAllRenterData(uuid);
    cacheTowns.getTrustData().removeAllTrustedData(uuid);
    cacheTowns.getCitizenData().removeCitizen(cacheTowns.getJoinedTownOwner(uuid), uuid);
  }

  public void startCollectionTask() {
    Bukkit.getAsyncScheduler().runAtFixedRate(towns, (scheduledTask) -> {
        if (cacheServer.getLastCollectionTime() < System.currentTimeMillis()) {
          cacheServer.setLastCollectionTime(System.currentTimeMillis() + CoreUtil.millisecondsToMidnightPST());
          startRentCollection();
          startTaxCollection();
        }
      },
      0,
      1,
      TimeUnit.MINUTES
    );
  }

  private void startRentCollection() {
    for (UUID uuid : cacheRenters.getRenterListData().getRenterList()) {
      double amount = 0;
      for (String chunk : cacheRenters.getRenterListData().getChunkList(uuid)) {
        if (!cacheRenters.isRented(chunk)) continue;
        final double rentCost = cacheRenters.getRentPrice(chunk);
        final double balance = EcoAPI.getBalance(uuid);
        if (balance < rentCost) {
          synchronized (CoreUtil.getSynchronizedThreadLock()) {
            Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
              cacheRenters.removeRenter(chunk);
              PlayerDataAPI.sendPlayerMessageIfOnline(uuid, Lang.PREFIX.getComponent(null).append(Lang.AUTO_EVICTED_WARNING_TARGET.getComponent(new String[]{chunk})));
              });
            continue;
          }
        }
        EcoAPI.removeBalance(uuid, rentCost);
        cacheBank.getData().addTownBalance(cacheChunks.getChunkOwner(chunk), rentCost);
        amount += rentCost;
      }
      PlayerDataAPI.sendPlayerMessageIfOnline(uuid, Lang.PREFIX.getComponent(null).append(Lang.AUTO_RENT_COLLECTION_MESSAGE.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
    }
    Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.RENT_COLLECTION_FINISHED.getComponent(null)));
  }

  private void startTaxCollection() {
    for (UUID playerID : cacheTowns.getAllPlayers()) {
      if (!cacheTowns.hasTown(playerID)) continue;
      final int claimAmount = cacheChunks.getChunkListData().getChunkClaims(playerID);
      final double cost = claimAmount * GlobalValue.CLAIM_TAX_AMOUNT.getValue();
      cacheBank.getData().removeTownBalance(playerID, cost);
      PlayerDataAPI.sendPlayerMessageIfOnline(playerID, Lang.PREFIX.getComponent(null).append(Lang.AUTO_TAX_COLLECTION_MESSAGE.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cost)})})));
    }
    Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.TAX_COLLECTION_FINISHED.getComponent(null)));
  }
}
