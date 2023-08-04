package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TownPlayerRoleData {

    private final CacheTowns cacheTowns;
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, String>> playerRoleCache = new ConcurrentHashMap<>();

    public TownPlayerRoleData(CacheTowns cacheTowns) {
        this.cacheTowns = cacheTowns;
    }

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

    public void deleteAllPlayerRoles(UUID uuid) {
        playerRoleCache.remove(uuid);
    }

    public void cachePlayerRoles(TownsTable townsTable) {
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
        final TownsTable townsTable = cacheTowns.getTownTable(uuid);
        if (townsTable.getPlayerRoles() == null) townsTable.setPlayerRoles(target + "+" + role);
        else townsTable.setPlayerRoles(townsTable.getPlayerRoles() + "," + target + "+" + role);
        setPlayerRoleCache(uuid, target, role);
        cacheTowns.updateTownsDatabase(townsTable);
    }

    public void removePlayerRole(UUID uuid, UUID target) {
        final TownsTable townsTable = cacheTowns.getTownTable(uuid);
        final Set<String> newRoles = Collections.synchronizedSet(new HashSet<>(List.of(townsTable.getPlayerRoles().split(","))));
        newRoles.remove(target + "+" + getPlayerRole(uuid, target));
        townsTable.setPlayerRoles(StringUtils.join(newRoles, ","));
        removePlayerRoleCache(uuid, target);
        cacheTowns.updateTownsDatabase(townsTable);
    }

}
