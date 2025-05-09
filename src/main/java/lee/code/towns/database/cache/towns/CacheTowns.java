package lee.code.towns.database.cache.towns;

import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.cache.towns.data.*;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.database.tables.TownsTable;
import lee.code.towns.enums.TownRole;
import lee.code.towns.utils.CoreUtil;
import lee.code.towns.utils.ItemUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheTowns extends DatabaseHandler {
  @Getter private final TownNameListData townNameListData;
  @Getter private final TownPermData permData;
  @Getter private final TownRoleData roleData;
  @Getter private final TownPlayerRoleData playerRoleData;
  @Getter private final TownRoleColorData roleColorData;
  @Getter private final TownTrustData trustData;
  @Getter private final TownCitizenData citizenData;
  private final ConcurrentHashMap<UUID, TownsTable> townsCache = new ConcurrentHashMap<>();

  public CacheTowns(DatabaseManager databaseManager) {
    super(databaseManager);
    this.permData = new TownPermData(databaseManager);
    this.roleData = new TownRoleData(databaseManager);
    this.playerRoleData = new TownPlayerRoleData(this);
    this.roleColorData = new TownRoleColorData(this);
    this.trustData = new TownTrustData(this);
    this.citizenData = new TownCitizenData(this);
    this.townNameListData = new TownNameListData(this);
  }

  public void createPlayerData(UUID uuid) {
    TownsTable townsTable = new TownsTable(uuid);
    setTownsTable(townsTable);
    createTownsDatabase(townsTable);
  }

  public TownsTable getTownTable(UUID uuid) {
    return townsCache.get(uuid);
  }

  public boolean hasTownsData(UUID uuid) {
    return townsCache.containsKey(uuid);
  }

  public void setTownsTable(TownsTable townsTable) {
    townsCache.put(townsTable.getUniqueId(), townsTable);
    playerRoleData.cachePlayerRoles(townsTable);
    roleColorData.cacheRoleColors(townsTable);
    trustData.cacheTrustedPlayers(townsTable);
    citizenData.cacheCitizenPlayers(townsTable);
    townNameListData.cacheTownName(townsTable);
  }

  public boolean hasTown(UUID uuid) {
    return getTownTable(uuid).getTown() != null;
  }

  public boolean hasTownOrJoinedTown(UUID uuid) {
    return getTownTable(uuid).getTown() != null || getTownTable(uuid).getJoinedTown() != null;
  }

  public String getTownName(UUID uuid) {
    return getTownTable(uuid).getTown();
  }

  public void setTownName(UUID uuid, String town) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setTown(town);
    updateTownsDatabase(townsTable);
  }

  public void setJoinedTown(UUID uuid, UUID townOwner) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setJoinedTown(townOwner);
    updateTownsDatabase(townsTable);
  }

  public void removeJoinedTown(UUID uuid) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setJoinedTown(null);
    updateTownsDatabase(townsTable);
  }

  public boolean hasJoinedTown(UUID uuid) {
    return getTownTable(uuid).getJoinedTown() != null;
  }

  public String getJoinedTownName(UUID uuid) {
    return getTownTable(getTownTable(uuid).getJoinedTown()).getTown();
  }

  public UUID getJoinedTownOwner(UUID uuid) {
    return getTownTable(uuid).getJoinedTown();
  }

  public UUID getTargetTownOwner(UUID target) {
    if (hasTown(target)) return target;
    else return getJoinedTownOwner(target);
  }

  public String getTargetTownName(UUID target) {
    if (!hasJoinedTown(target) && !hasTown(target)) return "None";
    return getTownName(getTargetTownOwner(target));
  }

  public String getTargetTownRole(UUID target) {
    if (!hasJoinedTown(target) && !hasTown(target)) return "None";
    if (hasTown(target)) {
      String mayorRole = CoreUtil.capitalize(TownRole.MAYOR.name());
      return roleColorData.getRoleColor(target, mayorRole) + mayorRole;
    }
    UUID owner = getJoinedTownOwner(target);
    String role = getPlayerRoleData().getPlayerRole(owner, target);
    return roleColorData.getRoleColor(owner, role) + role;
  }

  public Location getTownSpawn(UUID uuid) {
    TownsTable townsTable = getTownTable(uuid);
    if (townsTable.getSpawn() != null) return CoreUtil.parseLocation(townsTable.getSpawn());
    else return CoreUtil.parseLocation(getTownTable(townsTable.getJoinedTown()).getSpawn());
  }

  public void setTownSpawn(UUID uuid, Location location) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setSpawn(CoreUtil.serializeLocation(location));
    updateTownsDatabase(townsTable);
  }

  public int getMaxChunkClaims(UUID uuid) {
    int defaultAmount = 10;
    int size = citizenData.hasCitizens(uuid) ? citizenData.getCitizenAmount(uuid) : 0;
    return (size * 10 + defaultAmount + getBonusClaims(uuid));
  }

  public boolean isTownPublic(UUID uuid) {
    return getTownTable(uuid).isTownPublic();
  }

  public void setTownPublic(UUID uuid, boolean result) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setTownPublic(result);
    updateTownsDatabase(townsTable);
  }

  public void sendTownMessage(UUID uuid, Component message) {
    UUID owner = getTargetTownOwner(uuid);
    Set<UUID> players = ConcurrentHashMap.newKeySet();
    players.addAll(citizenData.getCitizensList(owner));
    players.add(owner);
    for (UUID citizen : players) PlayerDataAPI.sendPlayerMessageIfOnline(citizen, message);
  }

  public void createRole(UUID uuid, String role) {
    roleData.createRolePermissionData(uuid, role);
    roleColorData.addRoleColor(uuid, role, "&e", true);
  }

  public void deleteRole(UUID uuid, String role) {
    for (UUID citizen : citizenData.getCitizensList(uuid)) {
      if (getPlayerRoleData().getPlayerRole(uuid, citizen).equals(role)) {
        getPlayerRoleData().setPlayerRole(uuid, citizen, CoreUtil.capitalize(TownRole.CITIZEN.name()), false);
      }
    }
    roleColorData.removeRoleColor(uuid, role, false);
    roleData.deleteRole(uuid, role);
    updateTownsDatabase(getTownTable(uuid));
  }

  public int getBonusClaims(UUID uuid) {
    return getTownTable(uuid).getBonusClaims();
  }

  public void setBonusClaims(UUID uuid, int amount) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setBonusClaims(amount);
    updateTownsDatabase(townsTable);
  }

  public void addBonusClaims(UUID uuid, int amount) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setBonusClaims(townsTable.getBonusClaims() + amount);
    updateTownsDatabase(townsTable);
  }

  public void removeBonusClaims(UUID uuid, int amount) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setBonusClaims(Math.max(townsTable.getBonusClaims() - amount, 0));
    updateTownsDatabase(townsTable);
  }

  public void setBanner(UUID uuid, ItemStack banner) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setBanner(ItemUtil.serializeItemStack(banner));
    updateTownsDatabase(townsTable);
  }

  public ItemStack getBanner(UUID uuid) {
    return ItemUtil.parseItemStack(getTownTable(uuid).getBanner());
  }

  public boolean hasBanner(UUID uuid) {
    return getTownTable(uuid).getBanner() != null;
  }

  public boolean isFlying(UUID uuid) {
    return getTownTable(uuid).isFlying();
  }

  public void setFlying(UUID uuid, boolean result) {
    TownsTable townsTable = getTownTable(uuid);
    townsTable.setFlying(result);
    updateTownsDatabase(townsTable);
  }

  public Set<UUID> getAllPlayers() {
    return new HashSet<>(townsCache.keySet());
  }
}
