package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KickCMD extends SubCommand {
  private final Towns towns;

  public KickCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "kick";
  }

  @Override
  public String getDescription() {
    return "Kick a player from your town.";
  }

  @Override
  public String getSyntax() {
    return "/towns kick &f<player> <confirm/deny>";
  }

  @Override
  public String getPermission() {
    return "towns.command.kick";
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
    if (args.length < 2) {
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    final UUID playerID = player.getUniqueId();
    final String targetString = args[1];
    final OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetString);
    if (offlineTarget == null) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{targetString})));
      return;
    }
    final UUID targetID = offlineTarget.getUniqueId();
    final CacheManager cacheManager = towns.getCacheManager();
    if (!cacheManager.getCacheTowns().hasTownsData(targetID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PLAYER_DATA.getComponent(new String[]{targetString})));
      return;
    }
    if (!cacheManager.getCacheTowns().hasTown(playerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
      return;
    }
    if (!cacheManager.getCacheTowns().getCitizenData().isCitizen(playerID, targetID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.PREFIX.getComponent(null).append(Lang.ERROR_KICK_NOT_CITIZEN.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)}))));
      return;
    }
    if (args.length > 2) {
      switch (args[2].toLowerCase()) {
        case "confirm" -> {
          cacheManager.removeFromTown(targetID);
          cacheManager.getCacheTowns().sendTownMessage(playerID, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_KICK_TOWN.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_KICK_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
          if (offlineTarget.isOnline()) {
            final Player onlineTarget = offlineTarget.getPlayer();
            if (onlineTarget != null) onlineTarget.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_KICK_TARGET.getComponent(null)));
          }
        }
        case "deny" -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_KICK_DENY_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
        default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      }
      return;
    }
    CoreUtil.sendConfirmMessage(player, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_KICK_WARNING.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})),
      "/towns kick " + targetString,
      Lang.COMMAND_KICK_HOVER_CONFIRM.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)}),
      Lang.COMMAND_KICK_HOVER_DENY.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)}),
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
    if (args.length == 2) return StringUtil.copyPartialMatches(args[1], CoreUtil.getOnlinePlayers(), new ArrayList<>());
    return new ArrayList<>();
  }
}
