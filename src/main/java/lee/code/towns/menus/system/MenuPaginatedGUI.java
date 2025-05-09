package lee.code.towns.menus.system;

import lee.code.towns.menus.menu.menudata.MenuItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MenuPaginatedGUI implements InventoryHandler {
  protected MenuPlayerData menuPlayerData;
  private final List<Integer> border = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53);
  protected final List<Integer> paginatedSlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
  protected int page = 0;
  protected int index = 0;
  protected final int maxItemsPerPage = 28;
  @Getter private Inventory inventory;
  private final ItemStack fillerGlass = MenuItem.FILLER_GLASS.createItem();
  private final DelayManager delayManager = new DelayManager();
  private final Map<Integer, MenuButton> buttonMap = new HashMap<>();
  @Getter private final MenuSoundManager menuSoundManager = new MenuSoundManager();

  public MenuPaginatedGUI(MenuPlayerData menuPlayerData) {
    this.menuPlayerData = menuPlayerData;
  }

  public void setInventory() {
    this.inventory = createInventory();
  }

  public void clearInventory() {
    inventory.clear();
  }

  public void addButton(int slot, MenuButton button) {
    buttonMap.put(slot, button);
  }

  public void removeButton(int slot) {
    buttonMap.remove(slot);
  }

  public void clearButtons() {
    buttonMap.clear();
  }

  public void decorate(Player player) {
    buttonMap.forEach((slot, button) -> {
      final ItemStack icon = button.getIconCreator().apply(player);
      inventory.setItem(slot, icon);
    });
  }

  public void addFillerGlass() {
    for (int i = 0; i < getInventory().getSize(); i++) {
      inventory.setItem(i, fillerGlass);
    }
  }

  public void addBorderGlass() {
    for (int i : border) {
      inventory.setItem(i, fillerGlass);
    }
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    if (player.getInventory().equals(event.getClickedInventory())) {
      if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
        event.setCancelled(true);
        return;
      }
      return;
    }
    event.setCancelled(true);
    if (delayManager.hasDelayOrSchedule(player.getUniqueId())) return;
    int slot = event.getSlot();
    MenuButton button = buttonMap.get(slot);
    if (button != null) {
      button.getEventConsumer().accept(event);
    }
  }

  @Override
  public void onOpen(InventoryOpenEvent event) {
    decorate((Player) event.getPlayer());
  }

  @Override
  public void onClose(InventoryCloseEvent event) {
  }

  protected abstract Inventory createInventory();
}
