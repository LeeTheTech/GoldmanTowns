package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateCMD extends SubCommand {
  private final Towns towns;

  public CreateCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "create";
  }

  @Override
  public String getDescription() {
    return "Create a new town.";
  }

  @Override
  public String getSyntax() {
    return "/t create &f<name>";
  }

  @Override
  public String getPermission() {
    return "towns.command.create";
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
    final UUID playerID = player.getUniqueId();
    final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
    final CacheManager cacheManager = towns.getCacheManager();
    if (args.length <= 1) {
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    if (cacheManager.getCacheTowns().hasTown(playerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_HAS_TOWN.getComponent(new String[]{cacheManager.getCacheTowns().getTownName(playerID)})));
      return;
    }
    if (cacheManager.getCacheTowns().hasJoinedTown(playerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_HAS_JOINED_TOWN.getComponent(new String[]{cacheManager.getCacheTowns().getJoinedTownName(playerID)})));
      return;
    }
    final String town = CoreUtil.removeSpecialCharacters(CoreUtil.buildStringFromArgs(args, 1));
    if (cacheManager.getCacheTowns().getTownNameListData().isTownName(town)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_ALREADY_EXIST.getComponent(new String[]{town})));
      return;
    }
    if (cacheManager.getCacheChunks().isClaimed(chunk)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_CHUNK_CLAIMED.getComponent(new String[]{cacheManager.getChunkTownName(chunk)})));
      return;
    }
    cacheManager.createTown(playerID, town, player.getLocation());
    towns.getBorderParticleManager().spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.CLAIM, false);
    Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CREATE_ANNOUNCEMENT_TOWN_CREATED.getComponent(new String[]{ColorAPI.getNameColor(player.getUniqueId(), player.getName()), town})));
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CREATE_SUCCESS.getComponent(new String[]{town})));
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
