package lee.code.towns.menus.system;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {

    private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();
    private final Map<UUID, MenuPlayerData> playerMenuData = new HashMap<>();

    public void openMenu(MenuGUI gui, Player player) {
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

    public void handleClick(InventoryClickEvent event) {
        final InventoryHandler handler = activeInventories.get(event.getInventory());
        if (handler != null) handler.onClick(event);
    }

    public void handleOpen(InventoryOpenEvent event) {
        final InventoryHandler handler = activeInventories.get(event.getInventory());
        if (handler != null) handler.onOpen(event);

    }

    public void handleClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        final InventoryHandler handler = activeInventories.get(inventory);
        if (handler != null) {
            handler.onClose(event);
            unregisterInventory(inventory);
        }
    }
}
