package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.menus.menu.enums.MenuItem;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class FlagManager extends MenuGUI {

    private final Towns towns;

    public FlagManager(Towns towns, UUID uuid) {
        super(towns.getMenuManager().getMenuPlayerData(uuid));
        this.towns = towns;
        setInventory();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,27, Lang.MENU_FLAG_MANAGER_TITLE.getComponent(null));
    }

    @Override
    public void decorate(Player player) {
        addButton(10, createButton(MenuItem.FLAG_MANAGER_CHUNK, player));
        addButton(13, createButton(MenuItem.FLAG_MANAGER_GLOBAL, player));
        addButton(16, createButton(MenuItem.FLAG_MANAGER_ROLE, player));
        super.decorate(player);
    }

    private MenuButton createButton(MenuItem menuItem, Player player) {
        return new MenuButton()
                .creator(p -> menuItem.createItem())
                .consumer(event -> {
                    switch (menuItem.getMenuRout()) {
                        case FLAG_MANAGER_GLOBAL -> {
                            towns.getMenuManager().openMenu(new FlagManagerGlobal(menuPlayerData, towns), player);
                        }
                        case FLAG_MANAGER_CHUNK -> {
                            final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
                            if (!towns.getCacheManager().getCacheChunks().isClaimed(chunk) || !towns.getCacheManager().getCacheChunks().isChunkOwner(chunk, player.getUniqueId())) {
                                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_CHUNK_NOT_CLAIMED.getComponent(null)));
                                return;
                            }
                            towns.getMenuManager().openMenu(new FlagManagerChunk(menuPlayerData, towns, chunk), player);
                        }
                        case ROLE_SELECTION_MANAGER -> {
                            towns.getMenuManager().openMenu(new RoleSelectionManager(menuPlayerData, towns), player);
                        }
                    }
                });
    }
}
