package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
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
        return "Create a new town role or set a player's town role.";
    }

    @Override
    public String getSyntax() {
        return "&e/towns role &f<set/create> <options>";
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
        if (args.length > 1) {
            final CacheManager cacheManager = towns.getCacheManager();
            final String option = args[1].toLowerCase();
            final UUID owner = player.getUniqueId();
            if (!cacheManager.getCacheTowns().hasTown(owner)) {
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
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { playerName })));
                        return;
                    }
                    if (!cacheManager.getCacheTowns().getRoleData().getAllRoles(owner).contains(role)) {
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_SET_ROLE_NOT_FOUND.getComponent(new String[] { role })));
                        return;
                    }
                    if (!cacheManager.getCacheTowns().isCitizen(owner, targetUniqueID)) {
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_SET_PLAYER_NOT_CITIZEN.getComponent(new String[] { playerName })));
                        return;
                    }
                    cacheManager.getCacheTowns().getPlayerRoleData().setPlayerRole(owner, targetUniqueID, role);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_SET_SUCCESS.getComponent(new String[] { playerName, role })));
                }
                case "create" -> {
                    if (args.length < 3) {
                        player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_ROLE_CREATE_SYNTAX.getComponent()));
                        return;
                    }
                    final String role = CoreUtil.removeSpecialCharacters(CoreUtil.buildStringFromArgs(args, 2));
                    if (cacheManager.getCacheTowns().getRoleData().getAllRoles(owner).contains(role)) {
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ROLE_CREATE_ROLE_EXISTS.getComponent(new String[] { role })));
                        return;
                    }
                    cacheManager.getCacheTowns().getRoleData().createRole(owner, role);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ROLE_CREATE_SUCCESS.getComponent(new String[] { role })));
                }
                default -> player.sendMessage(Lang.USAGE.getComponent(null).append(CoreUtil.parseColorComponent(getSyntax())));
            }
        } else player.sendMessage(Lang.USAGE.getComponent(null).append(CoreUtil.parseColorComponent(getSyntax())));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
    }

    @Override
    public void performSender(CommandSender sender, String[] args) { }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        final CacheManager cacheManager = towns.getCacheManager();
        switch (args.length) {
            case 2 -> {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("set", "create"), new ArrayList<>());
            }
            case 3 -> {
                if (args[1].equalsIgnoreCase("set")) return StringUtil.copyPartialMatches(args[2], CoreUtil.getOnlinePlayers(), new ArrayList<>());
            }
            case 4 -> {
                if (sender instanceof Player player) {
                    if (args[1].equalsIgnoreCase("set")) return StringUtil.copyPartialMatches(args[3], cacheManager.getCacheTowns().getRoleData().getAllRoles(player.getUniqueId()), new ArrayList<>());
                }
            }
        }
        return new ArrayList<>();
    }
}
