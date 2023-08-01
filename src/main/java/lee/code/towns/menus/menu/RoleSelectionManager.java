package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.menu.enums.MenuItem;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.menus.system.MenuPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class RoleSelectionManager extends MenuGUI {

    private final Towns towns;

    public RoleSelectionManager(MenuPlayerData menuPlayerData, Towns towns) {
        super(menuPlayerData);
        this.towns = towns;
        setInventory();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,54, Lang.MENU_ROLE_SELECTION_MANAGER_TITLE.getComponent(null));
    }

    @Override
    public void decorate(Player player) {
        final List<String> roles = towns.getCacheManager().getCacheTowns().getAllRoles(player.getUniqueId());
        page = menuPlayerData.getPage();
        for (int i = 0; i < maxItemsPerPage; i++) {
            index = maxItemsPerPage * page + i;
            if(index >= roles.size()) break;
            if (roles.get(index) != null) {
                addButton(i, createRoleButton(player, roles.get(index)));
            }
        }
        super.decorate(player);
    }

    private MenuButton createRoleButton(Player player, String role) {
        return new MenuButton()
                .creator(p -> MenuItem.ROLE.createRoleItem(role))
                .consumer(e -> {
                    towns.getMenuManager().openMenu(new FlagManagerRole(menuPlayerData, towns, role), player);
                });
    }
}
