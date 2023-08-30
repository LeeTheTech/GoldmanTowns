package lee.code.towns.menus.system;

import lee.code.colors.ColorAPI;
import lee.code.towns.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class MenuLockManager {
  private final Map<UUID, String> townMenus = new HashMap<>();
  private final Map<UUID, String> chunkMenus = new HashMap<>();

  public void addTownMenuLock(UUID uuid, String town) {
    townMenus.put(uuid, town);
  }

  public void addChunkMenuLock(UUID uuid, String chunk) {
    chunkMenus.put(uuid, chunk);
  }

  public UUID getTownMenuEditor(String town) {
    final Optional<Map.Entry<UUID, String>> entryOptional = townMenus.entrySet().stream()
      .filter(entry -> entry.getValue().contains(town))
      .findFirst();
    return entryOptional.map(Map.Entry::getKey).orElse(null);
  }

  public UUID getChunkMenuEditor(String chunk) {
    final Optional<Map.Entry<UUID, String>> entryOptional = chunkMenus.entrySet().stream()
      .filter(entry -> entry.getValue().contains(chunk))
      .findFirst();
    return entryOptional.map(Map.Entry::getKey).orElse(null);
  }

  public boolean checkTownMenuLocked(Player player, String town) {
    final boolean result = townMenus.values().stream().anyMatch(value -> value.contains(town));
    if (result) {
      final OfflinePlayer offlineEditor = Bukkit.getOfflinePlayer(getTownMenuEditor(town));
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_TOWN_LOCKED.getComponent(new String[]{town, ColorAPI.getNameColor(offlineEditor.getUniqueId(), offlineEditor.getName())})));
    } else {
      addTownMenuLock(player.getUniqueId(), town);
    }
    return result;
  }

  public boolean checkChunkMenuLocked(Player player, String chunk) {
    final boolean result = chunkMenus.values().stream().anyMatch(value -> value.contains(chunk));
    if (result) {
      final OfflinePlayer offlineEditor = Bukkit.getOfflinePlayer(getChunkMenuEditor(chunk));
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_CHUNK_LOCKED.getComponent(new String[]{chunk, ColorAPI.getNameColor(offlineEditor.getUniqueId(), offlineEditor.getName())})));
    } else {
      addChunkMenuLock(player.getUniqueId(), chunk);
    }
    return result;
  }

  public void removeData(UUID uuid) {
    chunkMenus.remove(uuid);
    townMenus.remove(uuid);
  }
}
