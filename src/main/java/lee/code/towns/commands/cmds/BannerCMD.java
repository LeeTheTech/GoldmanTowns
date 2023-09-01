package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.menu.TownBannerMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BannerCMD extends SubCommand {
  private final Towns towns;

  public BannerCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "banner";
  }

  @Override
  public String getDescription() {
    return "Purchase your town banner.";
  }

  @Override
  public String getSyntax() {
    return "/towns banner";
  }

  @Override
  public String getPermission() {
    return "towns.command.banner";
  }

  @Override
  public boolean performAsync() {
    return false;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return false;
  }

  @Override
  public void perform(Player player, String[] args) {
    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    final UUID playerID = player.getUniqueId();
    if (!cacheTowns.hasTownOrJoinedTown(playerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }
    final UUID ownerID = cacheTowns.getTargetTownOwner(playerID);
    if (!cacheTowns.hasBanner(ownerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANNER_NOT_SET.getComponent(null)));
      return;
    }
    towns.getMenuManager().openMenu(new TownBannerMenu(towns), player);
  }

  @Override
  public void performConsole(CommandSender console, String[] args) {
    console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
  }

  @Override
  public void performSender(CommandSender sender, String[] args) {
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return new ArrayList<>();
  }
}
