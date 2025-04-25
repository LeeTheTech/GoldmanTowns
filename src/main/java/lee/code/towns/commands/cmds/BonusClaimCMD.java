package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BonusClaimCMD extends SubCommand {
  private final Towns towns;

  public BonusClaimCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "bonusclaim";
  }

  @Override
  public String getDescription() {
    return "Your town bonus claim info.";
  }

  @Override
  public String getSyntax() {
    return "/t bonusclaim &f<gift> <player> <amount>";
  }

  @Override
  public String getPermission() {
    return "towns.command.bonusclaim";
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
    int playerBonusClaims = towns.getCacheManager().getCacheTowns().getBonusClaims(player.getUniqueId());
    if (args.length == 1) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIM_SUCCESS.getComponent(new String[]{CoreUtil.parseValue(playerBonusClaims)})));
      return;
    }
    if (args.length < 4 || !args[1].equalsIgnoreCase("gift")) {
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    String targetString = args[2];
    UUID targetID = PlayerDataAPI.getUniqueId(targetString);
    if (targetID == null) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PLAYER_DATA.getComponent(new String[]{targetString})));
      return;
    }
    if (targetID.equals(player.getUniqueId())) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BONUS_CLAIM_GIFT_SELF.getComponent(null)));
      return;
    }
    String amountString = args[3];
    if (!CoreUtil.isPositiveIntNumber(amountString)){
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_VALUE_INVALID.getComponent(new String[]{amountString})));
      return;
    }
    int amountBeingSent = Integer.parseInt(amountString);
    if (amountBeingSent > playerBonusClaims) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BONUS_CLAIM_GIFT_INSUFFICIENT_BONUS_CLAIMS.getComponent(new String[]{CoreUtil.parseValue(playerBonusClaims), CoreUtil.parseValue(amountBeingSent)})));
      return;
    }
    CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    cacheTowns.removeBonusClaims(player.getUniqueId(), amountBeingSent);
    cacheTowns.addBonusClaims(targetID, amountBeingSent);
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIM_GIFT_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString), CoreUtil.parseValue(amountBeingSent)})));
    PlayerDataAPI.sendPlayerMessageIfOnline(targetID, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIM_GIFT_TARGET_SUCCESS.getComponent(new String[]{CoreUtil.parseValue(amountBeingSent), ColorAPI.getNameColor(player.getUniqueId(), player.getName())})));
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
    if (args.length == 3) return StringUtil.copyPartialMatches(args[2], CoreUtil.getOnlinePlayers(), new ArrayList<>());
    return new ArrayList<>();
  }
}
