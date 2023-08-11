package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.menus.menu.menudata.MenuItem;
import lee.code.towns.menus.system.MenuPlayerData;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class FlagManager extends MenuGUI {

    private final Towns towns;

    public FlagManager(MenuPlayerData menuPlayerData, Towns towns) {
        super(menuPlayerData);
        this.towns = towns;
        setInventory();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,27, Lang.MENU_FLAG_MANAGER_TITLE.getComponent(null));
    }

    @Override
    public void decorate(Player player) {
        addFillerGlass();
        addButton(10, createButton(MenuItem.FLAG_MANAGER_CHUNK, player));
        addButton(13, createButton(MenuItem.FLAG_MANAGER_GLOBAL, player));
        addButton(16, createButton(MenuItem.FLAG_MANAGER_ROLE, player));
        super.decorate(player);
    }

    private MenuButton createButton(MenuItem menuItem, Player player) {
        return new MenuButton()
                .creator(p -> menuItem.createItem())
                .consumer(event -> {
                    final CacheManager cacheManager = towns.getCacheManager();
                    final UUID uuid = player.getUniqueId();
                    switch (menuItem.getMenuRout()) {
                        case FLAG_MANAGER_GLOBAL -> {
                            final UUID owner = cacheManager.getCacheTowns().getTargetTownOwner(uuid);
                            if (!uuid.equals(owner)) {
                                final String role = cacheManager.getCacheTowns().getPlayerRoleData().getPlayerRole(owner, uuid);
                                if (!cacheManager.getCacheTowns().getRoleData().checkRolePermissionFlag(owner, role, Flag.CHANGE_GLOBAL_FLAGS)) {
                                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_GLOBAL_NO_PERMS.getComponent(null)));
                                    return;
                                }
                            }
                            towns.getMenuManager().openMenu(new FlagManagerGlobal(menuPlayerData, towns), player);
                        }
                        case FLAG_MANAGER_CHUNK -> {
                            final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
                            if (!cacheManager.getCacheChunks().isClaimed(chunk)) {
                                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_CHUNK_NOT_CLAIMED.getComponent(null)));
                                return;
                            }
                            if (!cacheManager.getCacheChunks().isChunkOwner(chunk, uuid)) {
                                final UUID chunkOwner = cacheManager.getCacheChunks().getChunkOwner(chunk);
                                final UUID townOwner = cacheManager.getCacheTowns().getTargetTownOwner(uuid);
                                if (!chunkOwner.equals(townOwner)) {
                                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_CHUNK_NOT_TOWN.getComponent(null)));
                                    return;
                                }
                                final String role = cacheManager.getCacheTowns().getPlayerRoleData().getPlayerRole(chunkOwner, uuid);
                                if (!cacheManager.getCacheTowns().getRoleData().checkRolePermissionFlag(chunkOwner, role, Flag.CHANGE_CHUNK_FLAGS)) {
                                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_CHUNK_NO_PERMS.getComponent(null)));
                                    return;
                                }
                            }
                            if (cacheManager.getCacheRenters().isRented(chunk)) {
                                if (!cacheManager.getCacheRenters().isPlayerRenting(uuid, chunk)) {
                                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_CHUNK_RENTED.getComponent(new String[] { chunk, cacheManager.getCacheRenters().getRenterName(chunk) })));
                                    return;
                                }
                            }
                            towns.getMenuManager().openMenu(new FlagManagerChunk(menuPlayerData, towns, chunk, true), player);
                        }
                        case ROLE_SELECTION_MANAGER -> {
                            final UUID owner = cacheManager.getCacheTowns().getTargetTownOwner(uuid);
                            if (!uuid.equals(owner)) {
                                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_FLAG_MANAGER_ROLE_NOT_OWNER.getComponent(null)));
                                return;
                            }
                            towns.getMenuManager().openMenu(new RoleSelectionManager(menuPlayerData, towns), player);
                        }
                    }
                });
    }
}
