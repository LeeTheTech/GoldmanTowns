package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.menu.menudata.FlagMenuItem;
import lee.code.towns.menus.menu.menudata.MenuItem;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FlagGlobalMenu extends MenuGUI {
  private final Towns towns;

  public FlagGlobalMenu(Towns towns) {
    this.towns = towns;
    setInventory();
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 45, Lang.MENU_FLAG_MANAGER_GLOBAL_TITLE.getComponent(null));
  }

  @Override
  public void onClose(InventoryCloseEvent e) {
    towns.getMenuManager().getMenuLockManager().removeData(e.getPlayer().getUniqueId());
  }

  @Override
  public void decorate(Player player) {
    addFillerGlass();
    UUID owner = towns.getCacheManager().getCacheTowns().getTargetTownOwner(player.getUniqueId());
    addButton(10, createFlagButton(player, FlagMenuItem.BREAK, owner));
    addButton(11, createFlagButton(player, FlagMenuItem.BUILD, owner));
    addButton(12, createFlagButton(player, FlagMenuItem.INTERACT, owner));
    addButton(13, createFlagButton(player, FlagMenuItem.DAMAGE, owner));
    addButton(14, createFlagButton(player, FlagMenuItem.PVP, owner));
    addButton(15, createFlagButton(player, FlagMenuItem.PVE, owner));
    addButton(16, createFlagButton(player, FlagMenuItem.MONSTER_SPAWNING, owner));
    addButton(20, createFlagButton(player, FlagMenuItem.REDSTONE, owner));
    addButton(21, createFlagButton(player, FlagMenuItem.EXPLOSION, owner));
    addButton(22, createFlagButton(player, FlagMenuItem.ICE_MELT, owner));
    addButton(23, createFlagButton(player, FlagMenuItem.TELEPORT, owner));
    addButton(24, createFlagButton(player, FlagMenuItem.FIRE_SPREAD, owner));
    addButton(40, backButton(player));
    super.decorate(player);
  }

  private MenuButton createFlagButton(Player player, FlagMenuItem flagMenuItem, UUID owner) {
    return new MenuButton()
      .creator(p -> flagMenuItem.createItem(towns.getCacheManager().getCacheTowns().getPermData().checkGlobalPermissionFlag(owner, flagMenuItem.getFlag())))
      .consumer(e -> {
        getMenuSoundManager().playClickSound(player);
        CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
        if (!cacheTowns.hasTownOrJoinedTown(owner)) {
          e.getWhoClicked().getInventory().close();
          return;
        }
        boolean newResult = !cacheTowns.getPermData().checkGlobalPermissionFlag(owner, flagMenuItem.getFlag());
        cacheTowns.getPermData().setGlobalPermissionFlag(owner, flagMenuItem.getFlag(), newResult);
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        ItemStack newItem = flagMenuItem.createItem(newResult);
        item.setItemMeta(newItem.getItemMeta());
        item.setType(newItem.getType());
      });
  }

  private MenuButton backButton(Player player) {
    return new MenuButton()
      .creator(p -> MenuItem.BACK.createItem())
      .consumer(e -> {
        getMenuSoundManager().playClickSound(player);
        towns.getMenuManager().openMenu(new FlagMenu(towns), player);
      });
  }
}
