package lee.code.towns.commands.cmds;

import lee.code.economy.EcoAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankCMD extends SubCommand {
  private final Towns towns;

  public BankCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "bank";
  }

  @Override
  public String getDescription() {
    return "Your town's bank balance.";
  }

  @Override
  public String getSyntax() {
    return "/t bank &f<withdraw/deposit> <amount>";
  }

  @Override
  public String getPermission() {
    return "towns.command.bank";
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
    CacheManager cacheManager = towns.getCacheManager();
    UUID playerID = player.getUniqueId();
    if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }
    UUID ownerID = cacheManager.getCacheTowns().getTargetTownOwner(playerID);
    if (args.length > 2) {
      String option = args[1].toLowerCase();
      String amountString = args[2];
      if (!CoreUtil.isPositiveDoubleNumber(amountString)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_VALUE_INVALID.getComponent(new String[]{amountString})));
        return;
      }
      double amount = Double.parseDouble(amountString);
      switch (option) {
        case "withdraw" -> {
          if (!playerID.equals(ownerID)) {
            String role = cacheManager.getCacheTowns().getPlayerRoleData().getPlayerRole(ownerID, playerID);
            if (!cacheManager.getCacheTowns().getRoleData().checkRolePermissionFlag(ownerID, role, Flag.WITHDRAW)) {
              player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANK_WITHDRAW_NO_PERMISSION.getComponent(null)));
              return;
            }
          }
          if (cacheManager.getCacheBank().getData().getTownBalance(ownerID) < amount) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANK_WITHDRAW_INSUFFICIENT_FUNDS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
            return;
          }
          cacheManager.getCacheBank().getData().removeTownBalance(ownerID, amount);
          EcoAPI.addBalance(playerID, amount);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BANK_WITHDRAW_SUCCESS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
        }
        case "deposit" -> {
          if (EcoAPI.getBalance(playerID) < amount) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANK_DEPOSIT_INSUFFICIENT_FUNDS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
            return;
          }
          EcoAPI.removeBalance(playerID, amount);
          cacheManager.getCacheBank().getData().addTownBalance(ownerID, amount);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BANK_DEPOSIT_SUCCESS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
        }
        default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      }
    } else {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BANK_SUCCESS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheManager.getCacheBank().getData().getTownBalance(ownerID))})})));
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
