package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetBannerCMD extends SubCommand {
  private final Towns towns;

  public SetBannerCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "setbanner";
  }

  @Override
  public String getDescription() {
    return "Set your town's banner.";
  }

  @Override
  public String getSyntax() {
    return "/t setbanner";
  }

  @Override
  public String getPermission() {
    return "towns.command.setbanner";
  }

  @Override
  public boolean performAsync() {
    return true;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return true;
  }

  @Override
  public void perform(Player player, String[] args) {
    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    final UUID uuid = player.getUniqueId();
    if (!cacheTowns.hasTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
    }
    final ItemStack handItem = player.getInventory().getItemInMainHand();
    if (!(handItem.getType().name().endsWith("BANNER"))) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_SET_BANNER_NOT_BANNER.getComponent(null)));
      return;
    }
    cacheTowns.setBanner(uuid, handItem);
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_SET_BANNER_SUCCESS.getComponent(null)));
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
