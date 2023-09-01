package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.chunks.CacheChunks;
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

public class FlagChunkMenu extends MenuGUI {
  private final Towns towns;
  private final String chunk;
  private final boolean backSupport;

  public FlagChunkMenu(Towns towns, String chunk, boolean backSupport) {
    this.towns = towns;
    this.chunk = chunk;
    this.backSupport = backSupport;
    setInventory();
  }

  @Override
  protected Inventory createInventory() {
    final int size = backSupport ? 54 : 45;
    return Bukkit.createInventory(null, size, Lang.MENU_FLAG_MANAGER_CHUNK_TITLE.getComponent(null));
  }

  @Override
  public void onClose(InventoryCloseEvent e) {
    towns.getMenuManager().getMenuLockManager().removeData(e.getPlayer().getUniqueId());
  }

  @Override
  public void decorate(Player player) {
    addFillerGlass();
    addButton(4, createFlagButton(FlagMenuItem.CHUNK_FLAGS_ENABLED));
    addButton(19, createFlagButton(FlagMenuItem.BREAK));
    addButton(20, createFlagButton(FlagMenuItem.BUILD));
    addButton(21, createFlagButton(FlagMenuItem.INTERACT));
    addButton(22, createFlagButton(FlagMenuItem.DAMAGE));
    addButton(23, createFlagButton(FlagMenuItem.PVP));
    addButton(24, createFlagButton(FlagMenuItem.PVE));
    addButton(25, createFlagButton(FlagMenuItem.MONSTER_SPAWNING));
    addButton(29, createFlagButton(FlagMenuItem.REDSTONE));
    addButton(30, createFlagButton(FlagMenuItem.EXPLOSION));
    addButton(31, createFlagButton(FlagMenuItem.ICE_MELT));
    addButton(32, createFlagButton(FlagMenuItem.TELEPORT));
    addButton(33, createFlagButton(FlagMenuItem.FIRE_SPREAD));
    if (backSupport) addButton(49, backButton(player));
    super.decorate(player);
  }

  private MenuButton createFlagButton(FlagMenuItem flagMenuItem) {
    return new MenuButton()
      .creator(p -> flagMenuItem.createItem(towns.getCacheManager().getCacheChunks().getChunkPermData().checkChunkPermissionFlag(chunk, flagMenuItem.getFlag())))
      .consumer(e -> {
        final CacheChunks cacheChunks = towns.getCacheManager().getCacheChunks();
        if (!cacheChunks.isClaimed(chunk)) {
          e.getWhoClicked().getInventory().close();
          return;
        }
        final boolean newResult = !cacheChunks.getChunkPermData().checkChunkPermissionFlag(chunk, flagMenuItem.getFlag());
        cacheChunks.getChunkPermData().setChunkPermissionFlag(chunk, flagMenuItem.getFlag(), newResult);
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
        towns.getMenuManager().openMenu(new FlagMenu(towns), player);
      });
  }
}
