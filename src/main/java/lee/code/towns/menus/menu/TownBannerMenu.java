package lee.code.towns.menus.menu;

import lee.code.economy.EcoAPI;
import lee.code.towns.Towns;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.system.MenuButton;
import lee.code.towns.menus.system.MenuGUI;
import lee.code.towns.utils.CoreUtil;
import lee.code.towns.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TownBannerMenu extends MenuGUI {
  private final Towns towns;

  public TownBannerMenu(Towns towns) {
    this.towns = towns;
    setInventory();
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 27, Lang.MENU_BANNER_TITLE.getComponent(null));
  }

  @Override
  public void decorate(Player player) {
    addFillerGlass();
    addButton(13, createBannerButton(player));
    super.decorate(player);
  }

  private MenuButton createBannerButton(Player player) {
    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    final UUID playerID = player.getUniqueId();
    final UUID owner = cacheTowns.getTargetTownOwner(playerID);
    final ItemStack banner = new ItemStack(cacheTowns.getBanner(owner));
    final ItemMeta bannerMeta = banner.getItemMeta();
    final double cost = 500;
    ItemUtil.setItemLore(bannerMeta, Lang.MENU_BANNER_BANNER_LORE.getString(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cost)})}));
    banner.setItemMeta(bannerMeta);
    return new MenuButton()
      .creator(p-> banner)
      .consumer(e -> {
        final ItemStack newBanner = new ItemStack(cacheTowns.getBanner(owner));
        if (!ItemUtil.canReceiveItems(player, newBanner, 1)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANNER_INVENTORY_SPACE.getComponent(null)));
          return;
        }
        final double balance = EcoAPI.getBalance(playerID);
        if (balance < cost) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANNER_INSUFFICIENT_FUNDS.getComponent(null)));
          return;
        }
        EcoAPI.removeBalance(playerID, cost);
        player.getInventory().addItem(newBanner);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_BANNER_PURCHASE_SUCCESS.getComponent(null)));
      });
  }
}
