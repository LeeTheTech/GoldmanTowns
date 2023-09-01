package lee.code.towns.menus.system;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {
  @Getter private final MenuLockManager menuLockManager = new MenuLockManager();
  private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();
  private final Map<UUID, MenuPlayerData> playerMenuData = new HashMap<>();

  public void openMenu(MenuGUI gui, Player player) {
    registerHandledInventory(gui.getInventory(), gui);
    player.openInventory(gui.getInventory());
  }

  public void openMenu(MenuPaginatedGUI gui, Player player) {
    registerHandledInventory(gui.getInventory(), gui);
    player.openInventory(gui.getInventory());
  }

  public MenuPlayerData getMenuPlayerData(UUID uuid) {
    if (!playerMenuData.containsKey(uuid)) playerMenuData.put(uuid, new MenuPlayerData(uuid));
    return playerMenuData.get(uuid);
  }

  public void registerHandledInventory(Inventory inventory, InventoryHandler handler) {
    activeInventories.put(inventory, handler);
  }

  public void unregisterInventory(Inventory inventory) {
    activeInventories.remove(inventory);
  }

  public void handleClick(InventoryClickEvent e) {
    final InventoryHandler handler = activeInventories.get(e.getInventory());
    if (handler != null) handler.onClick(e);
  }

  public void handleOpen(InventoryOpenEvent e) {
    final InventoryHandler handler = activeInventories.get(e.getInventory());
    if (handler != null) handler.onOpen(e);

  }

  public void handleClose(InventoryCloseEvent e) {
    final Inventory inventory = e.getInventory();
    final InventoryHandler handler = activeInventories.get(inventory);
    if (handler != null) {
      handler.onClose(e);
      unregisterInventory(inventory);
    }
  }

  public void handleQuit(PlayerQuitEvent e) {
    playerMenuData.remove(e.getPlayer().getUniqueId());
    menuLockManager.removeData(e.getPlayer().getUniqueId());
  }
}
