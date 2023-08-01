package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.menus.system.MenuPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

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
        return Bukkit.createInventory(null,54, Lang.MENU_FLAG_MANAGER_ROLE_TITLE.getComponent(null));
    }
}
