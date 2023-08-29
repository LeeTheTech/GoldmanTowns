package lee.code.towns.utils;

import lee.code.colors.ColorAPI;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import org.bukkit.entity.Player;

import java.util.*;

public class FlagUtil {
  private static final Set<Flag> roleFlag = Collections.synchronizedSet(new HashSet<>(List.of(
    Flag.CHANGE_CHUNK_FLAGS,
    Flag.CHANGE_GLOBAL_FLAGS,
    Flag.BUILD,
    Flag.BREAK,
    Flag.INTERACT,
    Flag.INVITE,
    Flag.TELEPORT
  )));

  public static void setPermissionFlag(PermissionTable permissionTable, Flag flag, boolean result) {
    switch (flag) {
      case BUILD -> permissionTable.setBuild(result);
      case INTERACT -> permissionTable.setInteract(result);
      case BREAK -> permissionTable.setBreakBlock(result);
      case DAMAGE -> permissionTable.setDamage(result);
      case PVP -> permissionTable.setPvp(result);
      case PVE -> permissionTable.setPve(result);
      case REDSTONE -> permissionTable.setRedstone(result);
      case EXPLOSION -> permissionTable.setExplosion(result);
      case MONSTER_SPAWNING -> permissionTable.setMobSpawning(result);
      case CHUNK_FLAGS_ENABLED -> permissionTable.setChunkFlagsEnabled(result);
      case TELEPORT -> permissionTable.setTeleport(result);
      case INVITE -> permissionTable.setInvite(result);
      case CHANGE_CHUNK_FLAGS -> permissionTable.setChangeChunkFlags(result);
      case CHANGE_GLOBAL_FLAGS -> permissionTable.setChangeGlobalFlags(result);
    }
  }

  public static boolean checkPermissionFlag(PermissionTable permissionTable, Flag flag) {
    switch (flag) {
      case BUILD -> {
        return permissionTable.isBuild();
      }
      case INTERACT -> {
        return permissionTable.isInteract();
      }
      case BREAK -> {
        return permissionTable.isBreakBlock();
      }
      case DAMAGE -> {
        return permissionTable.isDamage();
      }
      case PVP -> {
        return permissionTable.isPvp();
      }
      case PVE -> {
        return permissionTable.isPve();
      }
      case REDSTONE -> {
        return permissionTable.isRedstone();
      }
      case EXPLOSION -> {
        return permissionTable.isExplosion();
      }
      case MONSTER_SPAWNING -> {
        return permissionTable.isMobSpawning();
      }
      case CHUNK_FLAGS_ENABLED -> {
        return permissionTable.isChunkFlagsEnabled();
      }
      case TELEPORT -> {
        return permissionTable.isTeleport();
      }
      case INVITE -> {
        return permissionTable.isInvite();
      }
      case CHANGE_CHUNK_FLAGS -> {
        return permissionTable.isChangeChunkFlags();
      }
      case CHANGE_GLOBAL_FLAGS -> {
        return permissionTable.isChangeGlobalFlags();
      }
      default -> {
        return false;
      }
    }
  }

  public static boolean isRoleFlag(Flag flag) {
    return roleFlag.contains(flag);
  }

  public static void sendFlagErrorMessage(Player player, Flag flag, String townName, UUID renterID, String renterName) {
    if (renterID != null) {
      player.sendActionBar(Lang.ERROR_LOCATION_RENTER_PERMISSION.getComponent(new String[]{townName, ColorAPI.getNameColor(renterID, renterName), CoreUtil.capitalize(flag.name()), Lang.FALSE.getString()}));
    } else {
      player.sendActionBar(Lang.ERROR_LOCATION_PERMISSION.getComponent(new String[]{townName, CoreUtil.capitalize(flag.name()), Lang.FALSE.getString()}));
    }
  }
}
