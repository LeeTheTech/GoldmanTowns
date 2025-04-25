package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.Data;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.database.cache.towns.CacheTowns;
import lee.code.towns.enums.TownRole;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
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
    return "/t role &f<options>";
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
    if (args.length < 2) {
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    CacheTowns cacheTowns = towns.getCacheManager().getCacheTowns();
    String option = args[1].toLowerCase();
    UUID ownerID = player.getUniqueId();
    if (!cacheTowns.hasTown(ownerID)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
      return;
    }
    switch (option) {
      case "set" -> {
        if (args.length < 4) {
          player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ROLE_SET_SYNTAX.getString()}));
          return;
        }
        String targetString = args[2];
        String role = args[3];
        UUID targetID = PlayerDataAPI.getUniqueId(targetString);
        if (targetID == null) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PLAYER_DATA.getComponent(new String[]{targetString})));
          return;
        }
        if (!cacheTowns.getRoleData().getAllRoles(ownerID).contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_ROLE_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        if (!cacheTowns.getCitizenData().isCitizen(ownerID, targetID)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_SET_PLAYER_NOT_CITIZEN.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
          return;
        }
        if (cacheTowns.getPlayerRoleData().getPlayerRole(ownerID, targetID).equals(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_SET_PLAYER_ALREADY_HAS_ROLE.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString), cacheTowns.getRoleColorData().getRoleWithColor(ownerID, role)})));
          return;
        }
        cacheTowns.getPlayerRoleData().setPlayerRole(ownerID, targetID, role, true);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_SET_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString), cacheTowns.getRoleColorData().getRoleWithColor(ownerID, role)})));
      }
      case "create" -> {
        if (args.length < 3) {
          player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ROLE_CREATE_SYNTAX.getString()}));
          return;
        }
        String role = CoreUtil.shortenString(CoreUtil.removeSpecialCharacters(CoreUtil.buildStringFromArgs(args, 2)), 30).trim();
        if (role.isBlank()) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_CREATE_BLANK.getComponent(new String[]{cacheTowns.getRoleColorData().getRoleWithColor(ownerID, role)})));
          return;
        }
        List<String> roles = cacheTowns.getRoleData().getAllRoles(ownerID);
        if (roles.contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_CREATE_ROLE_EXISTS.getComponent(new String[]{cacheTowns.getRoleColorData().getRoleWithColor(ownerID, role)})));
          return;
        }
        if (roles.size() >= 7) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_CREATE_MAX_ROLES.getComponent(null)));
          return;
        }
        cacheTowns.createRole(ownerID, role);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_CREATE_SUCCESS.getComponent(new String[]{role})));
      }
      case "delete" -> {
        if (args.length < 3) {
          player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ROLE_DELETE_SYNTAX.getString()}));
          return;
        }
        String role = args[2];
        if (!cacheTowns.getRoleData().getAllRoles(ownerID).contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_ROLE_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        if (role.equals(CoreUtil.capitalize(TownRole.CITIZEN.name()))) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_DELETE_DEFAULT_ROLE.getComponent(null)));
          return;
        }
        String roleWithColor = cacheTowns.getRoleColorData().getRoleWithColor(ownerID, role);
        cacheTowns.deleteRole(ownerID, role);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_DELETE_SUCCESS.getComponent(new String[]{roleWithColor})));
      }
      case "color" -> {
        if (args.length < 4) {
          player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_ROLE_COLOR_SYNTAX.getString()}));
          return;
        }
        String role = args[2];
        String color = args[3];
        if (!cacheTowns.getRoleData().getAllRolesAndMayor(ownerID).contains(role)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_ROLE_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        Data data = towns.getData();
        if (!data.getColors().containsKey(color)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_COLOR_NOT_FOUND.getComponent(new String[]{role})));
          return;
        }
        cacheTowns.getRoleColorData().setRoleColor(ownerID, role, data.getColors().get(color), true);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_COLOR_SUCCESS.getComponent(new String[]{role, data.getColors().get(color), CoreUtil.capitalize(color)})));
      }
      default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
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
      CacheManager cacheManager = towns.getCacheManager();
      if (!cacheManager.getCacheTowns().hasTown(player.getUniqueId())) return new ArrayList<>();
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
