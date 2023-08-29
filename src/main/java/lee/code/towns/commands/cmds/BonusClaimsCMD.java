package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.towns.CacheTowns;
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

public class BonusClaimsCMD extends SubCommand {
  private final Towns towns;

  public BonusClaimsCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "bonusclaims";
  }

  @Override
  public String getDescription() {
    return "Set an amount of bonus claims for a player.";
  }

  @Override
  public String getSyntax() {
    return "/towns bonusclaims &f<set/remove/add> <player> <amount>";
  }

  @Override
  public String getPermission() {
    return "towns.command.bonusclaims";
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
    performSender(player, args);
  }

  @Override
  public void performConsole(CommandSender console, String[] args) {
    performSender(console, args);
  }

  @Override
  public void performSender(CommandSender sender, String[] args) {
    if (args.length < 4) {
      sender.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    final String option = args[1].toLowerCase();
    final String targetString = args[2];
    final String amountString = args[3];
    if (!CoreUtil.isPositiveIntNumber(amountString)) {
      sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_VALUE_INVALID.getComponent(new String[]{amountString})));
      return;
    }
    final int amount = Integer.parseInt(amountString);
    final OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetString);
    if (offlineTarget == null) {
      sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{targetString})));
      return;
    }
    final UUID targetID = offlineTarget.getUniqueId();

    switch (option) {
      case "set" -> {
        cacheTowns.setBonusClaims(targetID, amount);
        sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIMS_SET_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString), CoreUtil.parseValue(amount)})));
      }
      case "add" -> {
        cacheTowns.addBonusClaims(targetID, amount);
        sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIMS_ADD_SUCCESS.getComponent(new String[]{CoreUtil.parseValue(amount), ColorAPI.getNameColor(targetID, targetString)})));
      }
      case "remove" -> {
        cacheTowns.removeBonusClaims(targetID, amount);
        sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIMS_REMOVE_SUCCESS.getComponent(new String[]{CoreUtil.parseValue(amount), ColorAPI.getNameColor(targetID, targetString)})));
      }
      default -> sender.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    if (args.length == 3) return StringUtil.copyPartialMatches(args[2], CoreUtil.getOnlinePlayers(), new ArrayList<>());
    else return new ArrayList<>();
  }
}
