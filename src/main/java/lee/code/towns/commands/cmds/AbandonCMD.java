package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbandonCMD extends SubCommand {
  private final Towns towns;

  public AbandonCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "abandon";
  }

  @Override
  public String getDescription() {
    return "Abandon your town, this will completely delete your town.";
  }

  @Override
  public String getSyntax() {
    return "/towns abandon &f<confirm/deny>";
  }

  @Override
  public String getPermission() {
    return "towns.command.abandon";
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
    final CacheManager cacheManager = towns.getCacheManager();
    final AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
    final UUID uuid = player.getUniqueId();
    if (!cacheManager.getCacheTowns().hasTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
      return;
    }
    if (autoClaimManager.isAutoClaiming(uuid)) autoClaimManager.removeAutoClaiming(uuid);
    final String town = cacheManager.getCacheTowns().getTownName(uuid);
    if (args.length > 1) {
      switch (args[1].toLowerCase()) {
        case "confirm" -> {
          cacheManager.getCacheTowns().sendTownMessage(uuid, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDON_TOWN_MESSAGE.getComponent(null)));
          cacheManager.deleteTown(uuid);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDON_SUCCESS.getComponent(new String[]{town})));
        }
        case "deny" -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDON_DENY.getComponent(null)));
        default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ABANDON_OPTION_SYNTAX.getString()}));
      }
    } else {
      CoreUtil.sendConfirmMessage(player, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDON_WARNING.getComponent(new String[]{cacheManager.getCacheTowns().getTownName(uuid)})),
        "/towns abandon",
        Lang.CONFIRM_ABANDON_HOVER.getComponent(null),
        Lang.DENY_ABANDON_HOVER.getComponent(null),
        true
      );
    }
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
