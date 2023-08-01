package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.menu.enums.FlagMenuItem;
import lee.code.towns.menus.menu.enums.MenuItem;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.menus.system.MenuPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FlagManagerGlobal extends MenuGUI {

    private final Towns towns;

    public FlagManagerGlobal(MenuPlayerData menuPlayerData, Towns towns) {
        super(menuPlayerData);
        this.towns = towns;
        setInventory();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,45, Lang.MENU_FLAG_MANAGER_GLOBAL_TITLE.getComponent(null));
    }

    @Override
    public void decorate(Player player) {
        addFillerGlass();
        final UUID uuid = player.getUniqueId();
        addButton(10, createFlagButton(FlagMenuItem.BREAK, uuid));
        addButton(11, createFlagButton(FlagMenuItem.BUILD, uuid));
        addButton(12, createFlagButton(FlagMenuItem.INTERACT, uuid));
        addButton(13, createFlagButton(FlagMenuItem.DAMAGE, uuid));
        addButton(14, createFlagButton(FlagMenuItem.PVP, uuid));
        addButton(15, createFlagButton(FlagMenuItem.PVE, uuid));
        addButton(16, createFlagButton(FlagMenuItem.MONSTER_SPAWNING, uuid));
        addButton(21, createFlagButton(FlagMenuItem.REDSTONE, uuid));
        addButton(22, createFlagButton(FlagMenuItem.EXPLOSION, uuid));
        addButton(23, createFlagButton(FlagMenuItem.TELEPORT, uuid));
        addButton(40, backButton(player));
        super.decorate(player);
    }

    private MenuButton createFlagButton(FlagMenuItem flagMenuItem, UUID uuid) {
        return new MenuButton()
                .creator(p -> flagMenuItem.createItem(towns.getCacheManager().getCacheTowns().checkGlobalPermissionFlag(uuid, flagMenuItem.getFlag())))
                .consumer(e -> {
                    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
                    final boolean newResult = !cacheTowns.checkGlobalPermissionFlag(uuid, flagMenuItem.getFlag());
                    cacheTowns.setGlobalPermissionFlag(uuid, flagMenuItem.getFlag(), newResult);
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
                    towns.getMenuManager().openMenu(new FlagManager(menuPlayerData, towns), player);
                });
    }
}
