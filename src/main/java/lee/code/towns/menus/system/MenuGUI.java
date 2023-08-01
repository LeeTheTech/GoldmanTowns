package lee.code.towns.menus.system;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class MenuGUI implements InventoryHandler {
    protected MenuPlayerData menuPlayerData;
    protected int page = 0;
    protected int index = 0;
    protected int maxItemsPerPage = 45;
    private Inventory inventory;
    private final DelayManager delayManager = new DelayManager();
    private final Map<Integer, MenuButton> buttonMap = new HashMap<>();

    public MenuGUI(MenuPlayerData menuPlayerData) {
        this.menuPlayerData = menuPlayerData;
    }

    public void setInventory() {
        this.inventory = createInventory();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addButton(int slot, MenuButton button) {
        buttonMap.put(slot, button);
    }

    public void decorate(Player player) {
        buttonMap.forEach((slot, button) -> {
            final ItemStack icon = button.getIconCreator().apply(player);
            inventory.setItem(slot, icon);
        });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (delayManager.hasDelayOrSchedule(event.getWhoClicked().getUniqueId())) return;
        final int slot = event.getSlot();
        final MenuButton button = buttonMap.get(slot);
        if (button != null) {
            button.getEventConsumer().accept(event);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.decorate((Player) event.getPlayer());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    protected abstract Inventory createInventory();
}
