package lee.code.towns.menus.menu;

import lee.code.towns.Towns;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.menu.menudata.MenuItem;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoleSelectionMenu extends MenuGUI {
  private final Towns towns;
  private final ArrayList<Integer> roleSlots = new ArrayList<>(List.of(10, 11, 12, 13, 14, 15, 16));
  private int roleIndex = 0;

  public RoleSelectionMenu(Towns towns) {
    this.towns = towns;
    setInventory();
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 36, Lang.MENU_ROLE_SELECTION_MANAGER_TITLE.getComponent(null));
  }

  @Override
  public void decorate(Player player) {
    addFillerGlass();
    List<String> roles = towns.getCacheManager().getCacheTowns().getRoleData().getAllRoles(player.getUniqueId());
    Collections.sort(roles);
    for (int roleSlot : roleSlots) {
      if (roles.size() > roleIndex) addButton(roleSlot, createRoleButton(player, roles.get(roleIndex)));
      else getInventory().setItem(roleSlot, new ItemStack(Material.AIR));
      roleIndex++;
    }
    addButton(31, backButton(player));
    super.decorate(player);
  }

  private MenuButton createRoleButton(Player player, String role) {
    return new MenuButton()
      .creator(p -> MenuItem.ROLE.createRoleItem(role))
      .consumer(e -> {
        getMenuSoundManager().playClickSound(player);
        CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
        if (!cacheTowns.hasTown(player.getUniqueId())) {
          e.getWhoClicked().getInventory().close();
          return;
        }
        towns.getMenuManager().openMenu(new FlagRoleMenu(towns, role), player);
      });
  }

  private MenuButton backButton(Player player) {
    return new MenuButton()
      .creator(p -> MenuItem.BACK.createItem())
      .consumer(e -> {
        getMenuSoundManager().playClickSound(player);
        towns.getMenuManager().openMenu(new FlagMenu(towns), player);
      });
  }
}
