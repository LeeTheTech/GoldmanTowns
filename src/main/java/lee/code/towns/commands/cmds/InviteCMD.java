package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.InviteManager;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
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
    return "&e/towns invite &f<player> <accept/deny>";
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
    if (args.length <= 1) {
      player.sendMessage(Lang.USAGE.getComponent(null).append(CoreUtil.parseColorComponent(getSyntax())));
      return;
    }
    final CacheManager cacheManager = towns.getCacheManager();
    final InviteManager inviteManager = towns.getInviteManager();

    if (args.length > 2) {
      final String targetName = args[1];
      final String option = args[2].toLowerCase();
      final UUID targetID = Bukkit.getPlayerUniqueId(targetName);
      if (targetID == null) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{targetName})));
        return;
      }
      switch (option) {
        case "accept" -> {
          if (!inviteManager.hasActiveInvite(targetID, player.getUniqueId())) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_INVALID.getComponent(null)));
            return;
          }
          cacheManager.getCacheTowns().getCitizenData().addCitizen(targetID, player.getUniqueId());
          inviteManager.removeActiveInvite(targetID, player.getUniqueId());
          cacheManager.getCacheTowns().sendTownMessage(targetID, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_JOINED_TOWN.getComponent(new String[]{player.getName()})));
          final Player target = Bukkit.getPlayer(targetID);
          if (target != null && target.isOnline())
            target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_TARGET_SUCCESS.getComponent(new String[]{player.getName()})));
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_SUCCESS.getComponent(new String[]{targetName})));
        }
        case "deny" -> {
          if (!inviteManager.hasActiveInvite(targetID, player.getUniqueId())) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_INVALID.getComponent(null)));
            return;
          }
          inviteManager.removeActiveInvite(targetID, player.getUniqueId());
          final Player target = Bukkit.getPlayer(targetID);
          if (target != null && target.isOnline())
            target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_DENY_TARGET_SUCCESS.getComponent(new String[]{player.getName()})));
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_DENY_SUCCESS.getComponent(new String[]{targetName})));
        }
        default ->
          player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_INVITE_OPTION_SYNTAX.getComponent()));
      }
      return;
    }

    //TODO ADD ROLE PERMS FOR INVITES
    if (!cacheManager.getCacheTowns().hasTown(player.getUniqueId())) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_NO_TOWN.getComponent(null)));
      return;
    }
    final String targetName = args[1];
    if (!CoreUtil.getOnlinePlayers().contains(targetName)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_ONLINE.getComponent(new String[]{targetName})));
      return;
    }
    final Player target = Bukkit.getPlayer(targetName);
    if (target == null) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{targetName})));
      return;
    }
    if (target.equals(player)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_SELF.getComponent(null)));
      return;
    }
    if (cacheManager.getCacheTowns().hasTown(target.getUniqueId()) || cacheManager.getCacheTowns().hasJoinedTown(target.getUniqueId())) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_APART_OF_TOWN.getComponent(new String[]{targetName})));
      return;
    }
    if (inviteManager.hasActiveInvite(player.getUniqueId(), target.getUniqueId())) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_PENDING.getComponent(new String[]{targetName})));
      return;
    }

    inviteManager.setActiveInvite(player.getUniqueId(), target.getUniqueId());
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_SUCCESS.getComponent(new String[]{target.getName()})));

    CoreUtil.sendConfirmMessage(target, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_TARGET_SUCCESS.getComponent(new String[]{cacheManager.getCacheTowns().getTownName(player.getUniqueId())})),
      "/towns invite " + player.getName(),
      Lang.COMMAND_ACCEPT_INVITE_HOVER.getComponent(new String[]{player.getName()}),
      Lang.COMMAND_DENY_INVITE_HOVER.getComponent(new String[]{player.getName()}),
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
