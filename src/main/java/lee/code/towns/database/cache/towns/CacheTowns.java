package lee.code.towns.database.cache.towns;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.cache.towns.data.TownPermData;
import lee.code.towns.database.cache.towns.data.TownPlayerRoleData;
import lee.code.towns.database.cache.towns.data.TownRoleColorData;
import lee.code.towns.database.cache.towns.data.TownRoleData;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.database.tables.TownsTable;
import lee.code.towns.enums.TownRole;
import lee.code.towns.utils.CoreUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CacheTowns extends DatabaseHandler {

    @Getter private final TownPermData permData;
    @Getter private final TownRoleData roleData;
    @Getter private final TownPlayerRoleData playerRoleData;
    @Getter private final TownRoleColorData roleColorData;
    private final ConcurrentHashMap<UUID, TownsTable> townsCache = new ConcurrentHashMap<>();

    public CacheTowns(DatabaseManager databaseManager) {
        super(databaseManager);
        this.permData = new TownPermData(databaseManager);
        this.roleData = new TownRoleData(databaseManager);
        this.playerRoleData = new TownPlayerRoleData(this);
        this.roleColorData = new TownRoleColorData(this);
    }

    public void createPlayerData(UUID uuid) {
        final TownsTable townsTable = new TownsTable(uuid);
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.TOWN);
        setTownsTable(townsTable);
        permData.setPermissionTable(permissionTable);
        createPermissionDatabase(permissionTable);
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
    }

    public boolean hasTown(UUID uuid) {
        return getTownTable(uuid).getTown() != null;
    }

    public String getTownName(UUID uuid) {
        return getTownTable(uuid).getTown();
    }

    public void createNewTown(UUID uuid, String town, Location spawn) {
        final TownsTable townsTable = getTownTable(uuid);
        townsTable.setTown(town);
        townsTable.setSpawn(CoreUtil.serializeLocation(spawn));
        roleColorData.setDefaultRoleColor(uuid, false);
        updateTownsDatabase(townsTable);
        roleData.createDefaultRolePermissionTable(uuid);
    }

    public void setTownName(UUID uuid, String town) {
        final TownsTable townsTable = getTownTable(uuid);
        townsTable.setTown(town);
        updateTownsDatabase(townsTable);
    }

    public void setJoinedTown(UUID uuid, UUID townOwner) {
        final TownsTable townsTable = getTownTable(uuid);
        townsTable.setJoinedTown(townOwner);
        updateTownsDatabase(townsTable);
    }

    public void removeJoinedTown(UUID uuid) {
        final TownsTable townsTable = getTownTable(uuid);
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
            final String mayorRole = CoreUtil.capitalize(TownRole.MAYOR.name());
            return roleColorData.getRoleColor(target, mayorRole) + mayorRole;
        }
        final UUID owner = getJoinedTownOwner(target);
        final String role = getPlayerRoleData().getPlayerRole(owner, target);
        return roleColorData.getRoleColor(owner, role) + role;
    }

    public boolean isTownNameTaken(String name) {
        return townsCache.values().stream()
                .anyMatch(playerData -> playerData.getTown() != null && playerData.getTown().equals(name));
    }

    public boolean hasCitizens(UUID uuid) {
        return getTownTable(uuid).getTownCitizens() != null;
    }

    public String getCitizens(UUID uuid) {
        return getTownTable(uuid).getTownCitizens();
    }

    public Set<UUID> getCitizensList(UUID uuid) {
        if (!hasCitizens(uuid)) return Collections.synchronizedSet(new HashSet<>());
        final Set<String> list = Collections.synchronizedSet(new HashSet<>(List.of(getTownTable(uuid).getTownCitizens().split(","))));
        return list.stream()
                .map(str -> {
                    try {
                        return UUID.fromString(str);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public String getCitizenNames(UUID uuid) {
        if (getTownTable(uuid).getTownCitizens() == null) return "None";
        final String[] split = getTownTable(uuid).getTownCitizens().split(",");
        final Set<String> playerNames = Collections.synchronizedSet(new HashSet<>());
        for (String citizen : split) {
            final OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(UUID.fromString(citizen));
            playerNames.add(oPlayer.getName());
        }
        return StringUtils.join(playerNames, ", ");
    }

    public boolean isCitizen(UUID owner, UUID target) {
        if (getTownTable(owner).getTownCitizens() == null) return false;
        return getTownTable(owner).getTownCitizens().contains(target.toString());
    }

    public void addCitizen(UUID owner, UUID target) {
        final TownsTable townsTable = getTownTable(owner);
        if (townsTable.getTownCitizens() == null) townsTable.setTownCitizens(target.toString());
        else townsTable.setTownCitizens(townsTable.getTownCitizens() + "," + target);
        final String role = CoreUtil.capitalize(TownRole.CITIZEN.name());
        playerRoleData.addPlayerRole(owner, target, role, false);
        updateTownsDatabase(townsTable);
        setJoinedTown(target, owner);
    }

    public void removeCitizen(UUID owner, UUID target) {
        final TownsTable townsTable = getTownTable(owner);
        final Set<String> citizens = Collections.synchronizedSet(new HashSet<>(List.of(townsTable.getTownCitizens().split(","))));
        citizens.remove(target.toString());
        if (citizens.isEmpty()) townsTable.setTownCitizens(null);
        else townsTable.setTownCitizens(StringUtils.join(citizens, ","));
        playerRoleData.removePlayerRole(owner, target, false);
        updateTownsDatabase(townsTable);
        removeJoinedTown(target);
    }

    public Location getTownSpawn(UUID uuid) {
        final TownsTable townsTable = getTownTable(uuid);
        if (townsTable.getSpawn() != null) return CoreUtil.parseLocation(townsTable.getSpawn());
        else return CoreUtil.parseLocation(getTownTable(townsTable.getJoinedTown()).getSpawn());
    }

    public void setTownSpawn(UUID uuid, Location location) {
        final TownsTable townsTable = getTownTable(uuid);
        townsTable.setSpawn(CoreUtil.serializeLocation(location));
        updateTownsDatabase(townsTable);
    }

    public int getMaxChunkClaims(UUID uuid) {
        final int defaultAmount = 10;
        final int size = hasCitizens(uuid) ? getTownTable(uuid).getTownCitizens().split(",").length : 0;
        return (size * 2 + defaultAmount);
    }

    public boolean isTownPublic(UUID uuid) {
        return getTownTable(uuid).isTownPublic();
    }

    public void setTownPublic(UUID uuid, boolean result) {
        final TownsTable townsTable = getTownTable(uuid);
        townsTable.setTownPublic(result);
        updateTownsDatabase(townsTable);
    }

    public void sendTownMessage(UUID uuid, Component message) {
        final UUID owner = getTargetTownOwner(uuid);
        final Set<UUID> players = getCitizensList(owner);
        players.add(owner);
        players.forEach(citizen -> {
            final OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(citizen);
            if (oPlayer.isOnline()) {
                final Player player = oPlayer.getPlayer();
                if (player != null) player.sendMessage(message);
            }
        });
    }

    public void leaveTown(UUID uuid) {
        removeCitizen(getJoinedTownOwner(uuid), uuid);
    }

    public void createRole(UUID uuid, String role) {
        roleData.createRolePermissionData(uuid, role);
        roleColorData.addRoleColor(uuid, role, "&e", true);
    }

    public void deleteRole(UUID uuid, String role) {
        for (UUID citizen : getCitizensList(uuid)) {
            if (getPlayerRoleData().getPlayerRole(uuid, citizen).equals(role)) {
                getPlayerRoleData().setPlayerRole(uuid, citizen, CoreUtil.capitalize(TownRole.CITIZEN.name()), false);
            }
        }
        roleColorData.removeRoleColor(uuid, role, false);
        roleData.deleteRole(uuid, role);
        updateTownsDatabase(getTownTable(uuid));
    }
}
