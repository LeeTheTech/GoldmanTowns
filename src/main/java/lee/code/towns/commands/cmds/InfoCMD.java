package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.chunks.CacheChunks;
import lee.code.towns.database.cache.server.CacheServer;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    final CacheChunks cacheChunks = towns.getCacheManager().getCacheChunks();
    final CacheServer cacheServer = towns.getCacheManager().getCacheServer();
    final UUID uuid = player.getUniqueId();
    if (!cacheTowns.hasTownOrJoinedTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }

    final List<Component> lines = new ArrayList<>();
    final UUID owner = cacheTowns.getTargetTownOwner(uuid);
    final String status = cacheTowns.isTownPublic(owner) ? Lang.PUBLIC.getString() : Lang.PRIVATE.getString();
    lines.add(Lang.COMMAND_INFO_HEADER.getComponent(null));
    lines.add(Component.text(""));
    lines.add(Lang.COMMAND_INFO_TOWN_PUBLIC.getComponent(new String[]{status}));
    lines.add(Lang.COMMAND_INFO_TOWN_NAME.getComponent(new String[]{cacheTowns.getTownName(owner)}));
    lines.add(Lang.COMMAND_INFO_TOWN_OWNER.getComponent(new String[]{ColorAPI.getNameColor(owner, Bukkit.getOfflinePlayer(owner).getName())}));
    lines.add(Lang.COMMAND_INFO_TOWN_BANK.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheTowns.getBankBalance(owner))})}));
    lines.add(Lang.COMMAND_INFO_TOWN_CITIZENS.getComponent(new String[]{String.valueOf(cacheTowns.getCitizenData().getCitizenAmount(owner))}));
    lines.add(Lang.COMMAND_INFO_TOWN_CHUNKS.getComponent(new String[]{CoreUtil.parseValue(cacheChunks.getChunkListData().getChunkClaims(owner)), CoreUtil.parseValue(cacheTowns.getMaxChunkClaims(owner))}));
    lines.add(Lang.COMMAND_INFO_TOWN_BONUS_CLAIMS.getComponent(new String[]{CoreUtil.parseValue(cacheTowns.getBonusClaims(owner))}));
    lines.add(Lang.COMMAND_INFO_TOWN_OUTPOSTS.getComponent(new String[]{CoreUtil.parseValue(cacheChunks.getChunkOutpostData().getOutpostAmount(owner)), CoreUtil.parseValue(cacheChunks.getChunkOutpostData().getMaxOutpostAmount())}));
    lines.add(Lang.COMMAND_INFO_TOWN_RENT.getComponent(new String[]{CoreUtil.parseTime(cacheServer.getNextRentCollectionTime())}));
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
    return new ArrayList<>();
  }
}
