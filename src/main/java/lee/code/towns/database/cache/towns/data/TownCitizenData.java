package lee.code.towns.database.cache.towns.data;

import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.database.tables.TownsTable;
import lee.code.towns.enums.TownRole;
import lee.code.towns.utils.CoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TownCitizenData {

    private final CacheTowns cacheTowns;
    private final ConcurrentHashMap<UUID, Set<UUID>> citizenCache = new ConcurrentHashMap<>();

    public TownCitizenData(CacheTowns cacheTowns) {
        this.cacheTowns = cacheTowns;
    }

    private void setCitizenCache(UUID uuid, UUID citizen) {
        if (citizenCache.containsKey(uuid)) {
            citizenCache.get(uuid).add(citizen);
        } else {
            final Set<UUID> citizens = ConcurrentHashMap.newKeySet();
            citizens.add(citizen);
            citizenCache.put(uuid, citizens);
        }
    }

    private void removeCitizenCache(UUID uuid, UUID target) {
        citizenCache.get(uuid).remove(target);
        if (citizenCache.get(uuid).isEmpty()) citizenCache.remove(uuid);
    }

    public void cacheCitizenPlayers(TownsTable townsTable) {
        if (townsTable.getTownCitizens() == null) return;
        final String[] players = townsTable.getTownCitizens().split(",");
        for (String player : players) setCitizenCache(townsTable.getUniqueId(), UUID.fromString(player));
    }

    public void deleteCitizenCache(UUID uuid) {
        citizenCache.remove(uuid);
    }

    public boolean hasCitizens(UUID uuid) {
        return citizenCache.containsKey(uuid);
    }

    public int getCitizenAmount(UUID uuid) {
        if (!citizenCache.containsKey(uuid)) return 0;
        return citizenCache.get(uuid).size();
    }

    public Set<UUID> getCitizensList(UUID uuid) {
        if (!hasCitizens(uuid)) return ConcurrentHashMap.newKeySet();
        return citizenCache.get(uuid);
    }

    public String getCitizenNames(UUID uuid) {
        if (cacheTowns.getTownTable(uuid).getTownCitizens() == null) return "None";
        final Set<String> playerNames = ConcurrentHashMap.newKeySet();
        for (UUID citizen : getCitizensList(uuid)) {
            final OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(citizen);
            playerNames.add(oPlayer.getName());
        }
        return StringUtils.join(playerNames, ", ");
    }

    public boolean isCitizen(UUID owner, UUID target) {
        if (!citizenCache.containsKey(owner)) return false;
        return citizenCache.get(owner).contains(target);
    }

    public void addCitizen(UUID owner, UUID target) {
        final TownsTable townsTable = cacheTowns.getTownTable(owner);
        if (townsTable.getTownCitizens() == null) townsTable.setTownCitizens(target.toString());
        else townsTable.setTownCitizens(townsTable.getTownCitizens() + "," + target);
        setCitizenCache(owner, target);
        cacheTowns.getPlayerRoleData().addPlayerRole(owner, target, CoreUtil.capitalize(TownRole.CITIZEN.name()), false);
        cacheTowns.updateTownsDatabase(townsTable);
        cacheTowns.setJoinedTown(target, owner);
    }

    public void removeCitizen(UUID owner, UUID target) {
        final TownsTable townsTable = cacheTowns.getTownTable(owner);
        final Set<String> citizens = Collections.synchronizedSet(new HashSet<>(List.of(townsTable.getTownCitizens().split(","))));
        citizens.remove(target.toString());
        if (citizens.isEmpty()) townsTable.setTownCitizens(null);
        else townsTable.setTownCitizens(StringUtils.join(citizens, ","));
        removeCitizenCache(owner, target);
        cacheTowns.getPlayerRoleData().removePlayerRole(owner, target, false);
        cacheTowns.updateTownsDatabase(townsTable);
        cacheTowns.removeJoinedTown(target);
    }
}
