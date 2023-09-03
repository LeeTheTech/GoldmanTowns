package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminCMD extends SubCommand {
  private final Towns towns;

  public AdminCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "admin";
  }

  @Override
  public String getDescription() {
    return "Admin town commands, this is only for staff members.";
  }

  @Override
  public String getSyntax() {
    return "/t admin &f<unclaim/delete/bonusclaims>";
  }

  @Override
  public String getPermission() {
    return "towns.command.admin";
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
    if (args.length < 2) {
      sender.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    final CacheManager cacheManager = towns.getCacheManager();
    final String option = args[1].toLowerCase();
    switch (option) {
      case "bonusclaims" -> {
        if (args.length < 5) {
          sender.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ADMIN_BONUSCLAIMS_SYNTAX.getString()}));
          return;
        }
        final String bonusOption = args[2].toLowerCase();
        final String targetString = args[3];
        final String amountString = args[4];
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
        switch (bonusOption) {
          case "set" -> {
            cacheManager.getCacheTowns().setBonusClaims(targetID, amount);
            sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIMS_SET_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString), CoreUtil.parseValue(amount)})));
          }
          case "add" -> {
            cacheManager.getCacheTowns().addBonusClaims(targetID, amount);
            sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIMS_ADD_SUCCESS.getComponent(new String[]{CoreUtil.parseValue(amount), ColorAPI.getNameColor(targetID, targetString)})));
          }
          case "remove" -> {
            cacheManager.getCacheTowns().removeBonusClaims(targetID, amount);
            sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BONUS_CLAIMS_REMOVE_SUCCESS.getComponent(new String[]{CoreUtil.parseValue(amount), ColorAPI.getNameColor(targetID, targetString)})));
          }
          default -> sender.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ADMIN_BONUSCLAIMS_SYNTAX.getString()}));
        }
      }
      case "unclaim" -> {
        if (sender instanceof Player player) {
          final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
          if (!cacheManager.getCacheChunks().isClaimed(chunk)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ADMIN_UNCLAIM_NOT_CLAIMED.getComponent(new String[]{chunk})));
            return;
          }
          if (cacheManager.getCacheChunks().isEstablishedChunk(chunk)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ADMIN_UNCLAIM_ESTABLISHED_CHUNK.getComponent(null)));
            return;
          }
          final UUID ownerID = cacheManager.getCacheChunks().getChunkOwner(chunk);
          if (!cacheManager.getCacheChunks().isUnclaimSafe(ownerID, chunk)) {
            final OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(ownerID);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ADMIN_UNCLAIM_UNSAFE.getComponent(new String[]{chunk, ColorAPI.getNameColor(ownerID, offlineOwner.getName())})));
            return;
          }
          cacheManager.getCacheChunks().unclaimChunk(chunk);
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_UNCLAIM_SUCCESS.getComponent(new String[]{chunk})));
          return;
        }
        sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
      }
      case "delete" -> {
        if (args.length < 3) {
          sender.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ADMIN_DELETE_SYNTAX.getString()}));
          return;
        }
        final String targetString = args[2].toLowerCase();
        final OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetString);
        if (offlineTarget == null) {
          sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{targetString})));
          return;
        }
        final UUID targetID =  offlineTarget.getUniqueId();
        if (!cacheManager.getCacheTowns().hasTownsData(targetID)) {
          sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PLAYER_DATA.getComponent(new String[]{targetString})));
          return;
        }
        if (!cacheManager.getCacheTowns().hasTown(targetID)) {
          sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ADMIN_DELETE_NO_TOWN.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
          return;
        }
        cacheManager.deleteTown(targetID);
        sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_DELETE_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
      }
      default -> sender.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ADMIN_DELETE_SYNTAX.getString()}));
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    switch (args.length) {
      case 3 -> {
        if (args[1].equalsIgnoreCase("delete")) return StringUtil.copyPartialMatches(args[2], CoreUtil.getOnlinePlayers(), new ArrayList<>());
      }
      case 4 -> {
        if (args[1].equalsIgnoreCase("bonusclaims")) return StringUtil.copyPartialMatches(args[3], CoreUtil.getOnlinePlayers(), new ArrayList<>());
      }
    }
    return new ArrayList<>();
  }
}
