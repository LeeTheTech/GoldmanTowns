package lee.code.towns.database.cache;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;
import lee.code.towns.enums.PermissionType;
import lee.code.towns.database.tables.TownsTable;
import lee.code.towns.enums.TownRole;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheTowns {

    private final DatabaseManager databaseManager;
    private final ConcurrentHashMap<UUID, TownsTable> townsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, PermissionTable> permissionCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, PermissionTable>> rolePermissionCache = new ConcurrentHashMap<>();

    public CacheTowns(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //Player Data
    private void createPlayerDatabase(TownsTable townsTable) {
        databaseManager.createTownsTable(townsTable);
    }

    private void updatePlayerDatabase(TownsTable townsTable) {
        databaseManager.updateTownsTable(townsTable);
    }

    public void createPlayerData(UUID uuid) {
        final TownsTable townsTable = new TownsTable(uuid);
        final PermissionTable permissionTable = new PermissionTable(uuid, PermissionType.TOWN);
        setPlayerTable(townsTable);
        setPermissionTable(permissionTable);
        createPlayerDatabase(townsTable);
        createPermissionDatabase(permissionTable);
        createDefaultRolePermissionTable(uuid);
    }

    public boolean hasPlayerData(UUID uuid) {
        return townsCache.containsKey(uuid);
    }

    public void setPlayerTable(TownsTable townsTable) {
        townsCache.put(townsTable.getUniqueId(), townsTable);
    }

    public boolean hasTown(UUID uuid) {
        return townsCache.get(uuid).getTown() != null;
    }

    public String getTown(UUID uuid) {
        return townsCache.get(uuid).getTown();
    }

    public void setTown(UUID uuid, String town, Location spawn) {
        final TownsTable townsTable = townsCache.get(uuid);
        townsTable.setTown(town);
        townsTable.setSpawn(CoreUtil.serializeLocation(spawn));
        updatePlayerDatabase(townsTable);
    }

    public boolean hasJoinedTown(UUID uuid) {
        return townsCache.get(uuid).getJoinedTown() != null;
    }

    public String getJoinedTown(UUID uuid) {
        return townsCache.get(townsCache.get(uuid).getJoinedTown()).getTown();
    }

    public UUID getJoinedTownOwner(UUID uuid) {
        return townsCache.get(uuid).getJoinedTown();
    }

    public boolean isTownNameTaken(String name) {
        return townsCache.values().stream()
                .anyMatch(playerData -> playerData.getTown() != null && playerData.getTown().equals(name));
    }

    public boolean isCitizen(UUID owner, UUID target) {
        return townsCache.get(owner).getTownMembers().contains(target.toString());
    }

    public Location getTownSpawn(UUID uuid) {
        final TownsTable townsTable = townsCache.get(uuid);
        if (townsTable.getSpawn() != null) return CoreUtil.parseLocation(townsTable.getSpawn());
        else return CoreUtil.parseLocation(townsCache.get(townsTable.getJoinedTown()).getSpawn());
    }

    public void setTownSpawn(UUID uuid, Location location) {
        final TownsTable townsTable = townsCache.get(uuid);
        townsTable.setSpawn(CoreUtil.serializeLocation(location));
        updatePlayerDatabase(townsTable);
    }

    //Permission Data

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
        final PermissionTable permissionTable = permissionCache.get(uuid);
        switch (flag) {
            case BUILD -> permissionTable.setBuild(result);
            case INTERACT -> permissionTable.setInteract(result);
            case BREAK -> permissionTable.setBreakBlock(result);
            case DAMAGE -> permissionTable.setDamage(result);
            case PVP -> permissionTable.setPvp(result);
            case PVE -> permissionTable.setPve(result);
            case REDSTONE -> permissionTable.setRedstone(result);
            case EXPLOSION -> permissionTable.setExplosion(result);
            case MONSTER_SPAWNING -> permissionTable.setMobSpawning(result);
            case CHUNK_FLAGS_ENABLED -> permissionTable.setChunkFlagsEnabled(result);
            case TELEPORT -> permissionTable.setTeleport(result);
        }
        updatePermissionDatabase(permissionTable);
    }

    public boolean checkGlobalPermissionFlag(UUID uuid, Flag flag) {
        final PermissionTable permissionTable = permissionCache.get(uuid);
        switch (flag) {
            case BUILD -> {
                return permissionTable.isBuild();
            }
            case INTERACT -> {
                return permissionTable.isInteract();
            }
            case BREAK -> {
                return permissionTable.isBreakBlock();
            }
            case DAMAGE -> {
                return permissionTable.isDamage();
            }
            case PVP -> {
                return permissionTable.isPvp();
            }
            case PVE -> {
                return permissionTable.isPve();
            }
            case REDSTONE -> {
                return permissionTable.isRedstone();
            }
            case EXPLOSION -> {
                return permissionTable.isExplosion();
            }
            case MONSTER_SPAWNING -> {
                return permissionTable.isMobSpawning();
            }
            case CHUNK_FLAGS_ENABLED -> {
                return permissionTable.isChunkFlagsEnabled();
            }
            case TELEPORT -> {
                return permissionTable.isTeleport();
            }
            default -> {
                return false;
            }
        }
    }

    //Role Permission Data

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
}
