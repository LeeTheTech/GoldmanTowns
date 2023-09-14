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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FlagRoleMenu extends MenuGUI {
  private final Towns towns;
  private final String role;

  public FlagRoleMenu(Towns towns, String role) {
    this.towns = towns;
    this.role = role;
    setInventory();
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 45, Lang.MENU_FLAG_MANAGER_ROLE_TITLE.getComponent(null));
  }

  @Override
  public void decorate(Player player) {
    addFillerGlass();
    addButton(10, createFlagButton(player, FlagMenuItem.BREAK));
    addButton(11, createFlagButton(player, FlagMenuItem.BUILD));
    addButton(12, createFlagButton(player, FlagMenuItem.INTERACT));
    addButton(13, createFlagButton(player, FlagMenuItem.TELEPORT));
    addButton(14, createFlagButton(player, FlagMenuItem.INVITE));
    addButton(15, createFlagButton(player, FlagMenuItem.CHANGE_CHUNK_FLAGS));
    addButton(16, createFlagButton(player, FlagMenuItem.CHANGE_GLOBAL_FLAGS));
    addButton(21, createFlagButton(player, FlagMenuItem.WITHDRAW));
    addButton(22, createFlagButton(player, FlagMenuItem.CLAIM));
    addButton(23, createFlagButton(player, FlagMenuItem.UNCLAIM));
    addButton(40, backButton(player));
    super.decorate(player);
  }

  private MenuButton createFlagButton(Player player, FlagMenuItem flagMenuItem) {
    final UUID uuid = player.getUniqueId();
    return new MenuButton()
      .creator(p -> flagMenuItem.createItem(towns.getCacheManager().getCacheTowns().getRoleData().checkRolePermissionFlag(uuid, role, flagMenuItem.getFlag())))
      .consumer(e -> {
        getMenuSoundManager().playClickSound(player);
        final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
        if (!cacheTowns.hasTown(uuid)) {
          e.getWhoClicked().getInventory().close();
          return;
        }
        final boolean newResult = !cacheTowns.getRoleData().checkRolePermissionFlag(uuid, role, flagMenuItem.getFlag());
        cacheTowns.getRoleData().setRolePermissionFlag(uuid, role, flagMenuItem.getFlag(), newResult);
        final ItemStack item = e.getCurrentItem();
        if (item == null) return;
        final ItemStack newItem = flagMenuItem.createItem(newResult);
        item.setItemMeta(newItem.getItemMeta());
        item.setType(newItem.getType());
      });
  }

  private MenuButton backButton(Player player) {
    return new MenuButton()
      .creator(p -> MenuItem.BACK.createItem())
      .consumer(e -> {
        getMenuSoundManager().playClickSound(player);
        towns.getMenuManager().openMenu(new RoleSelectionMenu(towns), player);
      });
  }
}
