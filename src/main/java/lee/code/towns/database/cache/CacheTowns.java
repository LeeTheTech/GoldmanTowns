package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.database.tables.TownsTable;
import lee.code.towns.enums.TownRole;
import lee.code.towns.utils.CoreUtil;
import lee.code.towns.utils.PermissionUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheTowns {

    private final DatabaseManager databaseManager;
    private final ConcurrentHashMap<UUID, TownsTable> townsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, PermissionTable> permissionCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, PermissionTable>> rolePermissionCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, String>> playerRoleCache = new ConcurrentHashMap<>();
    public CacheTowns(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //Towns Data
    private void createTownsDatabase(TownsTable townsTable) {
        databaseManager.createTownsTable(townsTable);
    }

    private void updateTownsDatabase(TownsTable townsTable) {
        databaseManager.updateTownsTable(townsTable);
    }

    public void createPlayerData(UUID uuid) {
        final TownsTable townsTable = new TownsTable(uuid);
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.TOWN);
        setTownsTable(townsTable);
        setPermissionTable(permissionTable);
        createTownsDatabase(townsTable);
        createPermissionDatabase(permissionTable);
        createDefaultRolePermissionTable(uuid);
    }

    private TownsTable getTownTable(UUID uuid) {
        return townsCache.get(uuid);
    }

    public boolean hasTownsData(UUID uuid) {
        return townsCache.containsKey(uuid);
    }

    public void setTownsTable(TownsTable townsTable) {
        townsCache.put(townsTable.getUniqueId(), townsTable);
        cachePlayerRoles(townsTable);
    }

    public boolean hasTown(UUID uuid) {
        return getTownTable(uuid).getTown() != null;
    }

    public String getTown(UUID uuid) {
        return getTownTable(uuid).getTown();
    }

    public void setTown(UUID uuid, String town, Location spawn) {
        final TownsTable townsTable = getTownTable(uuid);
        townsTable.setTown(town);
        townsTable.setSpawn(CoreUtil.serializeLocation(spawn));
        updateTownsDatabase(townsTable);
    }

    public boolean hasJoinedTown(UUID uuid) {
        return getTownTable(uuid).getJoinedTown() != null;
    }

    public String getJoinedTown(UUID uuid) {
        return getTownTable(getTownTable(uuid).getJoinedTown()).getTown();
    }

    public UUID getJoinedTownOwner(UUID uuid) {
        return getTownTable(uuid).getJoinedTown();
    }

    public boolean isTownNameTaken(String name) {
        return townsCache.values().stream()
                .anyMatch(playerData -> playerData.getTown() != null && playerData.getTown().equals(name));
    }

    public boolean hasCitizens(UUID uuid) {
        return getTownTable(uuid).getTownMembers() != null;
    }

    public String getCitizens(UUID uuid) {
        return getTownTable(uuid).getTownMembers();
    }

    public String getCitizenNames(UUID uuid) {
        if (getTownTable(uuid).getTownMembers() == null) return "None";
        final String[] split = townsCache.get(uuid).getTownMembers().split(",");
        final HashSet<String> playerNames = new HashSet<>();
        for (String citizen : split) {
            final OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(UUID.fromString(citizen));
            playerNames.add(oPlayer.getName());
        }
        return StringUtils.join(playerNames, ", ");
    }

    public boolean isCitizen(UUID owner, UUID target) {
        if (getTownTable(owner).getTownMembers() == null) return false;
        return getTownTable(owner).getTownMembers().contains(target.toString());
    }

    public void addCitizen(UUID owner, UUID target) {
        final TownsTable townsTable = getTownTable(owner);
        if (townsTable.getTownMembers() == null) townsTable.setTownMembers(target.toString());
        else townsTable.setTownMembers(townsTable.getTownMembers() + "," + target);
        updateTownsDatabase(townsTable);
    }

    public void removeCitizen(UUID owner, UUID target) {
        final TownsTable townsTable = getTownTable(owner);
        final List<String> citizens = new ArrayList<>(List.of(townsTable.getTownMembers().split(",")));
        citizens.remove(target.toString());
        townsTable.setTownMembers(StringUtils.join(citizens, ","));
        updateTownsDatabase(townsTable);
    }

    public Location getTownSpawn(UUID uuid) {
        final TownsTable townsTable = getTownTable(uuid);
        if (townsTable.getSpawn() != null) return CoreUtil.parseLocation(townsTable.getSpawn());
        else return CoreUtil.parseLocation(townsCache.get(townsTable.getJoinedTown()).getSpawn());
    }

    public void setTownSpawn(UUID uuid, Location location) {
        final TownsTable townsTable = getTownTable(uuid);
        townsTable.setSpawn(CoreUtil.serializeLocation(location));
        updateTownsDatabase(townsTable);
    }

    public int getMaxChunkClaims(UUID uuid) {
        final int defaultAmount = 10;
        final int size = hasCitizens(uuid) ? getTownTable(uuid).getTownMembers().split(",").length : 0;
        return (size * 2 + defaultAmount);
    }

    //Permission Data

    private PermissionTable getPermissionTable(UUID uuid) {
        return permissionCache.get(uuid);
    }

    private void deletePermissionDatabase(PermissionTable permissionTable) {
        databaseManager.deletePermissionTable(permissionTable);
    }

    private void createPermissionDatabase(PermissionTable permissionTable) {
        databaseManager.createPermissionTable(permissionTable);
    }

    private void updatePermissionDatabase(PermissionTable permissionTable) {
        databaseManager.updatePermissionTable(permissionTable);
    }

    public void setPermissionTable(PermissionTable permissionTable) {
        permissionCache.put(permissionTable.getUniqueID(), permissionTable);
    }

    public void setGlobalPermissionFlag(UUID uuid, Flag flag, boolean result) {
        final PermissionTable permissionTable = getPermissionTable(uuid);
        PermissionUtil.setPermissionFlag(permissionTable, flag, result);
        updatePermissionDatabase(permissionTable);
    }

    public boolean checkGlobalPermissionFlag(UUID uuid, Flag flag) {
        final PermissionTable permissionTable = getPermissionTable(uuid);
        return PermissionUtil.checkPermissionFlag(permissionTable, flag);
    }

    //Role Permission Data

    private PermissionTable getRolePermissionTable(UUID uuid, String role) {
        return rolePermissionCache.get(uuid).get(role);
    }

    public void setRolePermissionFlag(UUID uuid, String role, Flag flag, boolean result) {
        final PermissionTable permissionTable = getRolePermissionTable(uuid, role);
        PermissionUtil.setPermissionFlag(permissionTable, flag, result);
        updatePermissionDatabase(permissionTable);
    }

    public boolean checkRolePermissionFlag(UUID uuid, String role, Flag flag) {
        final PermissionTable permissionTable =  getRolePermissionTable(uuid, role);
        return PermissionUtil.checkPermissionFlag(permissionTable, flag);
    }

    private void createDefaultRolePermissionTable(UUID uuid) {
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.ROLE);
        permissionTable.setRole(TownRole.CITIZEN.name());
        setRolePermissionTable(permissionTable);
        createPermissionDatabase(permissionTable);
    }

    public void setRolePermissionTable(PermissionTable permissionTable) {
        if (rolePermissionCache.containsKey(permissionTable.getUniqueID())) {
            rolePermissionCache.get(permissionTable.getUniqueID()).put(permissionTable.getRole(), permissionTable);
        } else {
            final ConcurrentHashMap<String, PermissionTable> newRolePermTable = new ConcurrentHashMap<>();
            newRolePermTable.put(permissionTable.getRole(), permissionTable);
            rolePermissionCache.put(permissionTable.getUniqueID(), newRolePermTable);
        }
    }

    public void setRolePermissionTable(List<PermissionTable> permissionTables) {
        permissionTables.forEach(this::setRolePermissionTable);
    }

    public void createRole(UUID uuid, String role) {
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.ROLE);
        permissionTable.setRole(role);
        setRolePermissionTable(permissionTable);
        createPermissionDatabase(permissionTable);
    }

    public void deleteRole(UUID uuid, String role) {
        deletePermissionDatabase(rolePermissionCache.get(uuid).get(role));
        rolePermissionCache.remove(uuid);
    }

    public List<String> getAllRoles(UUID uuid) {
        return new ArrayList<>(Collections.list(rolePermissionCache.get(uuid).keys()));
    }

    //Player Role Data

    private void setPlayerRoleCache(UUID uuid, UUID target, String role) {
        if (playerRoleCache.containsKey(uuid)) {
            playerRoleCache.get(uuid).put(target, role);
        } else {
            final ConcurrentHashMap<UUID, String> roles = new ConcurrentHashMap<>();
            roles.put(target, role);
            playerRoleCache.put(uuid, roles);
        }
    }

    private void removePlayerRoleCache(UUID uuid, UUID target) {
        playerRoleCache.get(uuid).remove(target);
    }

    private void cachePlayerRoles(TownsTable townsTable) {
        if (townsTable.getPlayerRoles() == null) return;
        final String[] pairs = townsTable.getPlayerRoles().split(",");
        for (String pair : pairs) {
            final String[] parts = pair.split("\\+");
            if (parts.length == 2) {
                final String target = parts[0];
                final String role = parts[1];
                setPlayerRoleCache(townsTable.getUniqueId(), UUID.fromString(target), role);
            }
        }
    }

    public String getPlayerRole(UUID uuid, UUID target) {
        return playerRoleCache.get(uuid).get(target);
    }

    public void setPlayerRole(UUID uuid, UUID target, String role) {
        final TownsTable townsTable = getTownTable(uuid);
        if (townsTable.getPlayerRoles() == null) townsTable.setPlayerRoles(target + "+" + role);
        else townsTable.setPlayerRoles(townsTable.getPlayerRoles() + "," + target + "+" + role);
        setPlayerRoleCache(uuid, target, role);
        updateTownsDatabase(townsTable);
    }

    public void removePlayerRole(UUID uuid, UUID target) {
        final TownsTable townsTable = getTownTable(uuid);
        final List<String> newRoles = new ArrayList<>(List.of(townsTable.getPlayerRoles().split(",")));
        newRoles.remove(target + "+" + getPlayerRole(uuid, target));
        townsTable.setPlayerRoles(StringUtils.join(newRoles, ","));
        removePlayerRoleCache(uuid, target);
        updateTownsDatabase(townsTable);
    }
}
