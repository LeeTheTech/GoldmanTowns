package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.menu.menudata.FlagMenuItem;
import lee.code.towns.menus.menu.menudata.MenuItem;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.menus.system.MenuPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FlagManagerRole extends MenuGUI {

    private final Towns towns;
    private final String role;

    public FlagManagerRole(MenuPlayerData menuPlayerData, Towns towns, String role) {
        super(menuPlayerData);
        this.towns = towns;
        this.role = role;
        setInventory();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,45, Lang.MENU_FLAG_MANAGER_ROLE_TITLE.getComponent(null));
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
        addButton(24, createFlagButton(FlagMenuItem.INVITE, uuid));
        addButton(40, backButton(player));
        super.decorate(player);
    }

    private MenuButton createFlagButton(FlagMenuItem flagMenuItem, UUID uuid) {
        return new MenuButton()
                .creator(p -> flagMenuItem.createItem(towns.getCacheManager().getCacheTowns().getRoleData().checkRolePermissionFlag(uuid, role, flagMenuItem.getFlag())))
                .consumer(e -> {
                    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
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
                    towns.getMenuManager().openMenu(new RoleSelectionManager(menuPlayerData, towns), player);
                });
    }
}
