package lee.code.towns.database.cache.towns;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.cache.towns.data.TownPermData;
import lee.code.towns.database.cache.towns.data.TownPlayerRoleData;
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
    private final ConcurrentHashMap<UUID, TownsTable> townsCache = new ConcurrentHashMap<>();

    public CacheTowns(DatabaseManager databaseManager) {
        super(databaseManager);
        this.permData = new TownPermData(databaseManager);
        this.roleData = new TownRoleData(databaseManager);
        this.playerRoleData = new TownPlayerRoleData(this);
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

    public String getJoinedTown(UUID uuid) {
        return getTownTable(getTownTable(uuid).getJoinedTown()).getTown();
    }

    public UUID getJoinedTownOwner(UUID uuid) {
        return getTownTable(uuid).getJoinedTown();
    }

    public UUID getPlayerTownOwner(UUID target) {
        if (hasTown(target)) return target;
        else return getJoinedTownOwner(target);
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
        final HashSet<String> playerNames = new HashSet<>();
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
        updateTownsDatabase(townsTable);
        setJoinedTown(target, owner);
        playerRoleData.setPlayerRole(owner, target, TownRole.CITIZEN.name());
    }

    public void removeCitizen(UUID owner, UUID target) {
        final TownsTable townsTable = getTownTable(owner);
        final Set<String> citizens = Collections.synchronizedSet(new HashSet<>(List.of(townsTable.getTownCitizens().split(","))));
        citizens.remove(target.toString());
        townsTable.setTownCitizens(StringUtils.join(citizens, ","));
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
        final UUID owner = getPlayerTownOwner(uuid);
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
}
