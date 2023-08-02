package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.CacheChunks;
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

public class FlagManagerChunk extends MenuGUI {

    private final Towns towns;
    private final String chunk;

    public FlagManagerChunk(MenuPlayerData menuPlayerData, Towns towns, String chunk) {
        super(menuPlayerData);
        this.towns = towns;
        this.chunk = chunk;
        setInventory();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,54, Lang.MENU_FLAG_MANAGER_CHUNK_TITLE.getComponent(null));
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
        addButton(30, createFlagButton(FlagMenuItem.REDSTONE));
        addButton(31, createFlagButton(FlagMenuItem.EXPLOSION));
        addButton(32, createFlagButton(FlagMenuItem.TELEPORT));
        addButton(49, backButton(player));
        super.decorate(player);
    }

    private MenuButton createFlagButton(FlagMenuItem flagMenuItem) {
        return new MenuButton()
                .creator(p -> flagMenuItem.createItem(towns.getCacheManager().getCacheChunks().checkChunkPermissionFlag(chunk, flagMenuItem.getFlag())))
                .consumer(e -> {
                    final CacheChunks cacheChunks = towns.getCacheManager().getCacheChunks();
                    final boolean newResult = !cacheChunks.checkChunkPermissionFlag(chunk, flagMenuItem.getFlag());
                    cacheChunks.setChunkPermissionFlag(chunk, flagMenuItem.getFlag(), newResult);
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
