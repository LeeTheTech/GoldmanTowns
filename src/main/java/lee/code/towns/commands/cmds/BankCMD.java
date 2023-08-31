package lee.code.towns.commands.cmds;

import lee.code.economy.EcoAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.towns.CacheTowns;
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
    return "/towns bank &f<withdraw/deposit> <amount>";
  }

  @Override
  public String getPermission() {
    return "towns.command.balance";
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
    if (!cacheTowns.hasTownOrJoinedTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }
    final UUID owner = cacheTowns.getTargetTownOwner(uuid);
    if (args.length > 2) {
      final String option = args[1].toLowerCase();
      final String amountString = args[2];
      if (!CoreUtil.isPositiveDoubleNumber(amountString)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_VALUE_INVALID.getComponent(new String[]{amountString})));
        return;
      }
      final int amount = Integer.parseInt(amountString);
      switch (option) {
        case "withdraw" -> {
          if (cacheTowns.getBankBalance(owner) < amount) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANK_WITHDRAW_INSUFFICIENT_FUNDS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
            return;
          }
          cacheTowns.removeBank(owner, amount);
          EcoAPI.addBalance(uuid, amount);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BANK_WITHDRAW_SUCCESS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
        }
        case "deposit" -> {
          if (EcoAPI.getBalance(uuid) < amount) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BANK_DEPOSIT_INSUFFICIENT_FUNDS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
            return;
          }
          EcoAPI.removeBalance(uuid, amount);
          cacheTowns.addBank(owner, amount);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BANK_DEPOSIT_SUCCESS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(amount)})})));
        }
        default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      }
    } else {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BANK_SUCCESS.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheTowns.getBankBalance(owner))})})));
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
