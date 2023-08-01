package lee.code.towns.menus.menu;

import lee.code.towns.lang.Lang;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.menus.system.MenuPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class FlagManagerRole extends MenuGUI {

    public FlagManagerRole(MenuPlayerData menuPlayerData) {
        super(menuPlayerData);
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,54, Lang.MENU_FLAG_MANAGER_ROLE_TITLE.getComponent(null));
    }
}
