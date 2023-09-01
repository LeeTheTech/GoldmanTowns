package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class LeaveCMD extends SubCommand {
  private final Towns towns;

  public LeaveCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "leave";
  }

  @Override
  public String getDescription() {
    return "Leave the town you're currently in.";
  }

  @Override
  public String getSyntax() {
    return "/t leave &f<confirm/deny>";
  }

  @Override
  public String getPermission() {
    return "towns.command.leave";
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
    final UUID uuid = player.getUniqueId();
    if (cacheManager.getCacheTowns().hasTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TOWN_OWNER_LEAVE.getComponent(null)));
      return;
    }
    if (!cacheManager.getCacheTowns().hasJoinedTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }
    final String town = cacheManager.getCacheTowns().getJoinedTownName(uuid);
    if (args.length > 1) {
      switch (args[1].toLowerCase()) {
        case "confirm" -> {
          cacheManager.getCacheTowns().sendTownMessage(uuid, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_LEAVE_PLAYER_LEFT_TOWN.getComponent(new String[]{ColorAPI.getNameColor(player.getUniqueId(), player.getName())})));
          cacheManager.removeFromTown(uuid);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_LEAVE_SUCCESS.getComponent(new String[]{town})));
        }
        case "deny" -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_LEAVE_DENY.getComponent(new String[]{town})));
        default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_LEAVE_OPTION_SYNTAX.getString()}));
      }
      return;
    }
    CoreUtil.sendConfirmMessage(player, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_LEAVE_WARNING.getComponent(new String[]{town})),
      "/towns leave",
      Lang.COMMAND_LEAVE_HOVER_CONFIRM.getComponent(new String[]{town}),
      Lang.COMMAND_LEAVE_HOVER_DENY.getComponent(new String[]{town}),
      true
    );
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
