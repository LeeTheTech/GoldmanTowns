package lee.code.towns.database.cache.handlers;

import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.enums.Flag;

public class FlagHandler {

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
            case CHANGE_FLAGS -> permissionTable.setChangeFlags(result);
            case INVITE -> permissionTable.setInvite(result);
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
            case CHANGE_FLAGS -> {
                return permissionTable.isChangeFlags();
            }
            default -> {
                return false;
            }
        }
    }

    public static boolean isRoleFlag(Flag flag) {
        return !flag.equals(Flag.DAMAGE) && !flag.equals(Flag.PVP) && !flag.equals(Flag.PVE);
    }
}
