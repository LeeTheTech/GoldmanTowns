package lee.code.towns.menus.system;

import lee.code.colors.ColorAPI;
import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.lang.Lang;
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

  public boolean checkTownMenuLocked(Player player, String town) {
    final Optional<Map.Entry<UUID, String>> result = townMenus.entrySet().stream()
      .filter(entry -> entry.getValue().equals(town))
      .findFirst();
    if (result.isPresent()) {
      final UUID editorID = result.get().getKey();
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_TOWN_LOCKED.getComponent(new String[]{town, ColorAPI.getNameColor(editorID, PlayerDataAPI.getName(editorID))})));
    } else {
      addTownMenuLock(player.getUniqueId(), town);
    }
    return result.isPresent();
  }

  public boolean checkChunkMenuLocked(Player player, String chunk) {
    final Optional<Map.Entry<UUID, String>> result = chunkMenus.entrySet().stream()
      .filter(entry -> entry.getValue().equals(chunk))
      .findFirst();
    if (result.isPresent()) {
      final UUID editorID = result.get().getKey();
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_CHUNK_LOCKED.getComponent(new String[]{chunk, ColorAPI.getNameColor(editorID, PlayerDataAPI.getName(editorID))})));
    } else {
      addChunkMenuLock(player.getUniqueId(), chunk);
    }
    return result.isPresent();
  }

  public void removeData(UUID uuid) {
    chunkMenus.remove(uuid);
    townMenus.remove(uuid);
  }
}
