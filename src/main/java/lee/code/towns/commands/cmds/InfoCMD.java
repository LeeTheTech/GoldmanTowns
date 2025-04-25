package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.bank.CacheBank;
import lee.code.towns.database.cache.chunks.CacheChunks;
import lee.code.towns.database.cache.server.CacheServer;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfoCMD extends SubCommand {
  private final Towns towns;

  public InfoCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "info";
  }

  @Override
  public String getDescription() {
    return "Info about your town.";
  }

  @Override
  public String getSyntax() {
    return "/t info";
  }

  @Override
  public String getPermission() {
    return "towns.command.info";
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
    CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    CacheChunks cacheChunks = towns.getCacheManager().getCacheChunks();
    CacheServer cacheServer = towns.getCacheManager().getCacheServer();
    CacheBank cacheBank = towns.getCacheManager().getCacheBank();
    UUID playerID = args.length > 1 ? PlayerDataAPI.getUniqueId(args[1]) : player.getUniqueId();
    if (playerID == null) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PLAYER_DATA.getComponent(new String[]{args[1]})));
      return;
    }
    if (!cacheTowns.hasTownOrJoinedTown(playerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }
    List<Component> lines = new ArrayList<>();
    UUID ownerID = cacheTowns.getTargetTownOwner(playerID);
    String status = cacheTowns.isTownPublic(ownerID) ? Lang.PUBLIC.getString() : Lang.PRIVATE.getString();
    lines.add(Lang.COMMAND_INFO_HEADER.getComponent(null));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_INFO_TOWN_TARGET_PLAYER.getComponent(new String[]{ColorAPI.getNameColor(playerID, PlayerDataAPI.getName(playerID))}));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_INFO_TOWN_PUBLIC.getComponent(new String[]{status}));
    lines.add(Lang.COMMAND_INFO_TOWN_NAME.getComponent(new String[]{cacheTowns.getTownName(ownerID)}));
    lines.add(Lang.COMMAND_INFO_TOWN_OWNER.getComponent(new String[]{ColorAPI.getNameColor(ownerID, PlayerDataAPI.getName(ownerID))}));
    lines.add(Lang.COMMAND_INFO_TOWN_BANK.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheBank.getData().getTownBalance(ownerID))})}));
    lines.add(Lang.COMMAND_INFO_TOWN_CITIZENS.getComponent(new String[]{String.valueOf(cacheTowns.getCitizenData().getCitizenAmount(ownerID))}));
    lines.add(Lang.COMMAND_INFO_TOWN_CHUNKS.getComponent(new String[]{CoreUtil.parseValue(cacheChunks.getChunkListData().getChunkClaims(ownerID)), CoreUtil.parseValue(cacheTowns.getMaxChunkClaims(ownerID))}));
    lines.add(Lang.COMMAND_INFO_TOWN_BONUS_CLAIMS.getComponent(new String[]{CoreUtil.parseValue(cacheTowns.getBonusClaims(ownerID))}));
    lines.add(Lang.COMMAND_INFO_TOWN_OUTPOSTS.getComponent(new String[]{CoreUtil.parseValue(cacheChunks.getChunkOutpostData().getOutpostAmount(ownerID)), CoreUtil.parseValue(cacheChunks.getChunkOutpostData().getMaxOutpostAmount())}));
    lines.add(Lang.COMMAND_INFO_TOWN_RENT.getComponent(new String[]{CoreUtil.parseTime(cacheServer.getNextCollectionTime())}));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_INFO_FOOTER.getComponent(null));
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
    if (args.length == 2) return StringUtil.copyPartialMatches(args[1], CoreUtil.getOnlinePlayers(), new ArrayList<>());
    return new ArrayList<>();
  }
}
