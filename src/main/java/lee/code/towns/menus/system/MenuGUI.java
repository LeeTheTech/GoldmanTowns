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

import java.util.HashMap;
import java.util.Map;

public abstract class MenuGUI implements InventoryHandler {
  @Getter private Inventory inventory;
  private final ItemStack fillerGlass = MenuItem.FILLER_GLASS.createItem();
  private final DelayManager delayManager = new DelayManager();
  private final Map<Integer, MenuButton> buttonMap = new HashMap<>();
  @Getter
  private final MenuSoundManager menuSoundManager = new MenuSoundManager();

  public void setInventory() {
    this.inventory = createInventory();
  }

  public void addButton(int slot, MenuButton button) {
    buttonMap.put(slot, button);
  }

  public void decorate(Player player) {
    buttonMap.forEach((slot, button) -> {
      ItemStack icon = button.getIconCreator().apply(player);
      inventory.setItem(slot, icon);
    });
  }

  public void addFillerGlass() {
    for (int i = 0; i < getInventory().getSize(); i++) {
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
