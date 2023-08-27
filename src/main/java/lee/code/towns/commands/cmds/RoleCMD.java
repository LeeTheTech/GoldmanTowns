package lee.code.towns.commands.cmds;

import lee.code.towns.Data;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class RoleCMD extends SubCommand {
  private final Towns towns;

  public RoleCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "role";
  }

  @Override
  public String getDescription() {
    return "Town role options.";
  }

  @Override
  public String getSyntax() {
    return "&e/towns role &f<set/create/delete/color> <options>";
  }

  @Override
  public String getPermission() {
    return "towns.command.role";
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
    final CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    final String option = args[1].toLowerCase();
    final UUID owner = player.getUniqueId();
    if (!cacheTowns.hasTown(owner)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
      return;
    }
    switch (option) {
      case "set" -> {
        if (args.length < 4) {
          player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_ROLE_SET_SYNTAX.getComponent()));
          return;
        }
        final String playerName = args[2];
        final String role = args[3];

        final UUID targetUniqueID = Bukkit.getPlayerUniqueId(playerName);
        if (targetUniqueID == null) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{playerName})));
          return;
        }
        if (!cacheTowns.getRoleData().getAllRoles(owner).contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_ROLE_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        if (!cacheTowns.getCitizenData().isCitizen(owner, targetUniqueID)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_SET_PLAYER_NOT_CITIZEN.getComponent(new String[]{playerName})));
          return;
        }
        if (cacheTowns.getPlayerRoleData().getPlayerRole(owner, targetUniqueID).equals(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_SET_PLAYER_ALREADY_HAS_ROLE.getComponent(new String[]{playerName, cacheTowns.getRoleColorData().getRoleWithColor(owner, role)})));
          return;
        }
        cacheTowns.getPlayerRoleData().setPlayerRole(owner, targetUniqueID, role, true);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_SET_SUCCESS.getComponent(new String[]{playerName, role})));
      }
      case "create" -> {
        if (args.length < 3) {
          player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_ROLE_CREATE_SYNTAX.getComponent()));
          return;
        }
        final String role = CoreUtil.removeSpecialCharacters(CoreUtil.buildStringFromArgs(args, 2));
        final List<String> roles = cacheTowns.getRoleData().getAllRoles(owner);
        if (roles.contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_CREATE_ROLE_EXISTS.getComponent(new String[]{role})));
          return;
        }
        if (roles.size() >= 7) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_CREATE_MAX_ROLES.getComponent(null)));
          return;
        }
        cacheTowns.createRole(owner, role);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_CREATE_SUCCESS.getComponent(new String[]{role})));
      }
      case "delete" -> {
        if (args.length < 3) {
          player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_ROLE_DELETE_SYNTAX.getComponent()));
          return;
        }
        final String role = args[2];
        if (!cacheTowns.getRoleData().getAllRoles(owner).contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_ROLE_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        cacheTowns.deleteRole(owner, role);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_DELETE_SUCCESS.getComponent(new String[]{role})));
      }
      case "color" -> {
        final Data data = towns.getData();
        if (args.length < 4) {
          player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_ROLE_COLOR_SYNTAX.getComponent()));
          return;
        }
        final String role = args[2];
        final String color = args[3];

        if (!cacheTowns.getRoleData().getAllRolesAndMayor(owner).contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_ROLE_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        if (!data.getColors().containsKey(color)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_COLOR_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        cacheTowns.getRoleColorData().setRoleColor(owner, role, data.getColors().get(color), true);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_COLOR_SUCCESS.getComponent(new String[]{role, data.getColors().get(color), CoreUtil.capitalize(color)})));
      }
      default -> player.sendMessage(Lang.USAGE.getComponent(null).append(CoreUtil.parseColorComponent(getSyntax())));
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
    if (sender instanceof Player player) {
      final CacheManager cacheManager = towns.getCacheManager();
      switch (args.length) {
        case 3 -> {
          if (args[1].equalsIgnoreCase("set"))
            return StringUtil.copyPartialMatches(args[2], CoreUtil.getOnlinePlayers(), new ArrayList<>());
          if (args[1].equalsIgnoreCase("color"))
            return StringUtil.copyPartialMatches(args[2], cacheManager.getCacheTowns().getRoleData().getAllRolesAndMayor(player.getUniqueId()), new ArrayList<>());
          if (args[1].equalsIgnoreCase("delete"))
            return StringUtil.copyPartialMatches(args[2], cacheManager.getCacheTowns().getRoleData().getAllRoles(player.getUniqueId()), new ArrayList<>());
        }
        case 4 -> {
          if (args[1].equalsIgnoreCase("set"))
            return StringUtil.copyPartialMatches(args[3], cacheManager.getCacheTowns().getRoleData().getAllRoles(player.getUniqueId()), new ArrayList<>());
          if (args[1].equalsIgnoreCase("color"))
            return StringUtil.copyPartialMatches(args[3], Collections.list(towns.getData().getColors().keys()), new ArrayList<>());
        }
      }
    }
    return new ArrayList<>();
  }
}
