package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.InviteManager;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class InviteCMD extends SubCommand {
  private final Towns towns;

  public InviteCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "invite";
  }

  @Override
  public String getDescription() {
    return "Invite an online player to your town.";
  }

  @Override
  public String getSyntax() {
    return "/t invite &f<player> <accept/deny>";
  }

  @Override
  public String getPermission() {
    return "towns.command.invite";
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
    if (args.length < 2) {
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    final UUID playerID = player.getUniqueId();
    final CacheManager cacheManager = towns.getCacheManager();
    final InviteManager inviteManager = towns.getInviteManager();

    if (args.length > 2) {
      final String targetString = args[1];
      final String option = args[2].toLowerCase();
      final UUID targetID = PlayerDataAPI.getUniqueId(targetString);
      if (targetID == null) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PLAYER_DATA.getComponent(new String[]{targetString})));
        return;
      }
      switch (option) {
        case "accept" -> {
          if (!inviteManager.hasActiveInvite(targetID, playerID)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_INVALID.getComponent(null)));
            return;
          }
          inviteManager.removeActiveInvite(targetID, playerID);
          cacheManager.getCacheTowns().getCitizenData().addCitizen(cacheManager.getCacheTowns().getTargetTownOwner(targetID), playerID);
          cacheManager.getCacheTowns().sendTownMessage(targetID, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_JOINED_TOWN.getComponent(new String[]{ColorAPI.getNameColor(player.getUniqueId(), player.getName())})));
          PlayerDataAPI.sendPlayerMessageIfOnline(targetID, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_TARGET_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(player.getUniqueId(), player.getName())})));
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
        }
        case "deny" -> {
          if (!inviteManager.hasActiveInvite(targetID, playerID)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_INVALID.getComponent(null)));
            return;
          }
          inviteManager.removeActiveInvite(targetID, playerID);
          PlayerDataAPI.sendPlayerMessageIfOnline(targetID, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_DENY_TARGET_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(player.getUniqueId(), player.getName())})));
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_DENY_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
        }
        default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      }
      return;
    }
    if (!cacheManager.getCacheTowns().hasTown(playerID)) {
      if (cacheManager.getCacheTowns().hasJoinedTown(playerID)) {
        final UUID ownerID = cacheManager.getCacheTowns().getJoinedTownOwner(playerID);
        final String role = cacheManager.getCacheTowns().getPlayerRoleData().getPlayerRole(ownerID, playerID);
        if (!cacheManager.getCacheTowns().getRoleData().checkRolePermissionFlag(ownerID, role, Flag.INVITE)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_INVALID_PERMISSION.getComponent(new String[]{CoreUtil.capitalize(Flag.INVITE.name())})));
          return;
        }
      } else {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_NO_TOWN.getComponent(null)));
        return;
      }
    }
    final String targetString = args[1];
    final Player target = PlayerDataAPI.getOnlinePlayer(targetString);
    if (target == null) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_ONLINE.getComponent(new String[]{targetString})));
      return;
    }
    if (target.equals(player)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_SELF.getComponent(null)));
      return;
    }
    if (cacheManager.getCacheTowns().hasTownOrJoinedTown(target.getUniqueId())) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_APART_OF_TOWN.getComponent(new String[]{ColorAPI.getNameColor(target.getUniqueId(), targetString)})));
      return;
    }
    if (inviteManager.hasActiveInvite(playerID, target.getUniqueId())) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_PENDING.getComponent(new String[]{ColorAPI.getNameColor(target.getUniqueId(), targetString)})));
      return;
    }
    inviteManager.setActiveInvite(playerID, target.getUniqueId());
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(target.getUniqueId(), target.getName())})));

    CoreUtil.sendConfirmMessage(target, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_TARGET_SUCCESS.getComponent(new String[]{cacheManager.getCacheTowns().getTargetTownName(playerID)})),
      "/towns invite " + player.getName(),
      Lang.COMMAND_ACCEPT_INVITE_HOVER.getComponent(new String[]{ColorAPI.getNameColor(playerID, player.getName())}),
      Lang.COMMAND_DENY_INVITE_HOVER.getComponent(new String[]{ColorAPI.getNameColor(playerID, player.getName())}),
      false
    );
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
