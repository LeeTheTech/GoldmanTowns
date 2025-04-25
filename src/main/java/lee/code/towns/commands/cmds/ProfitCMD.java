package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.GlobalValue;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfitCMD extends SubCommand {
  private final Towns towns;

  public ProfitCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "profit";
  }

  @Override
  public String getDescription() {
    return "Your town's daily profit.";
  }

  @Override
  public String getSyntax() {
    return "/t profit";
  }

  @Override
  public String getPermission() {
    return "towns.command.profit";
  }

  @Override
  public boolean performAsync() {
    return true;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return false;
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
    double rentProfit = 0;
    for (String chunk : cacheManager.getCacheRenters().getOwnerListData().getChunkList(ownerID)) {
      if (!cacheManager.getCacheRenters().isRented(chunk)) continue;
      rentProfit = rentProfit + cacheManager.getCacheRenters().getRentPrice(chunk);
    }
    double claimTax = GlobalValue.CLAIM_TAX_AMOUNT.getValue();
    int claims = cacheManager.getCacheChunks().getChunkListData().getChunkClaims(ownerID);
    double taxTotal = claimTax * claims;
    double profit = rentProfit - taxTotal;
    String profitColor = profit > 0 ? "&a" : "&c";
    List<Component> lines = new ArrayList<>();
    lines.add(Lang.COMMAND_PROFIT_TITLE.getComponent(null));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_PROFIT_RENT.getComponent(new String[]{"&a$" + CoreUtil.parseValue(rentProfit)}));
    lines.add(Lang.COMMAND_PROFIT_TAXES.getComponent(new String[]{"&c$-" + CoreUtil.parseValue(taxTotal)}));
    lines.add(Lang.COMMAND_PROFIT_PROFIT.getComponent(new String[]{profitColor + "$" + CoreUtil.parseValue(profit)}));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_PROFIT_FOOTER.getComponent(null));
    for (Component line : lines) player.sendMessage(line);
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
