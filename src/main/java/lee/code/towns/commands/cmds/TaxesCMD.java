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

public class TaxesCMD extends SubCommand {
  private final Towns towns;

  public TaxesCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "taxes";
  }

  @Override
  public String getDescription() {
    return "Tax info for your town.";
  }

  @Override
  public String getSyntax() {
    return "/t taxes";
  }

  @Override
  public String getPermission() {
    return "towns.command.taxes";
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
    double claimTax = GlobalValue.CLAIM_TAX_AMOUNT.getValue();
    int claims = cacheManager.getCacheChunks().getChunkListData().getChunkClaims(ownerID);
    List<Component> lines = new ArrayList<>();
    lines.add(Lang.COMMAND_TAXES_TITLE.getComponent(null));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_TAXES_CLAIMS.getComponent(new String[]{CoreUtil.parseValue(claims)}));
    lines.add(Lang.COMMAND_TAXES_COST.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(claimTax)})}));
    lines.add(Lang.COMMAND_TAXES_TOTAL.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(claims * claimTax)})}));
    lines.add(Lang.COMMAND_TAXES_TAX_COLLECTION.getComponent(new String[]{CoreUtil.parseTime(cacheManager.getCacheServer().getNextCollectionTime())}));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_TAXES_FOOTER.getComponent(null));
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
