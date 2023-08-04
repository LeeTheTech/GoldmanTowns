package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.InviteManager;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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
        return false;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length > 1) {
            final CacheManager cacheManager = towns.getCacheManager();
            final InviteManager inviteManager = towns.getInviteManager();

            if (args.length > 2) {
                final String targetName = args[1];
                final String option = args[2].toLowerCase();
                final UUID targetID = Bukkit.getPlayerUniqueId(targetName);
                if (targetID == null) {
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { targetName })));
                    return;
                }
                switch (option) {
                    case "accept" -> {
                        if (!inviteManager.hasActiveInvite(targetID, player.getUniqueId())) {
                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_INVALID.getComponent(null)));
                            return;
                        }
                        cacheManager.getCacheTowns().addCitizen(targetID, player.getUniqueId());
                        inviteManager.removeActiveInvite(targetID);
                        cacheManager.getCacheTowns().sendTownMessage(targetID, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_JOINED_TOWN.getComponent(new String[] { player.getName() } )));
                        final Player target = Bukkit.getPlayer(targetID);
                        if (target != null && target.isOnline()) target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_TARGET_SUCCESS.getComponent(new String[] { player.getName() })));
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_ACCEPT_SUCCESS.getComponent(new String[] { targetName })));
                    }
                    case "deny" -> {
                        if (!inviteManager.hasActiveInvite(targetID, player.getUniqueId())) {
                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_INVALID.getComponent(null)));
                            return;
                        }
                        inviteManager.removeActiveInvite(targetID);
                        final Player target = Bukkit.getPlayer(targetID);
                        if (target != null && target.isOnline()) target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_DENY_TARGET_SUCCESS.getComponent(new String[] { player.getName() })));
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_DENY_SUCCESS.getComponent(new String[] { targetName })));
                    }
                    default -> player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_INVITE_OPTION_SYNTAX.getComponent()));
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
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_ONLINE.getComponent(new String[] { targetName })));
                return;
            }
            final Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { targetName })));
                return;
            }
            if (inviteManager.hasActiveInvite(player.getUniqueId(), target.getUniqueId())) {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_INVITE_PENDING.getComponent(new String[] { targetName })));
                return;
            }

            inviteManager.setActiveInvite(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_SUCCESS.getComponent(new String[] { target.getName() })));

            final Component accept = Lang.ACCEPT.getComponent(null)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/towns invite " + player.getName() + " accept"))
                    .hoverEvent(Lang.COMMAND_ACCEPT_INVITE_HOVER.getComponent(new String[] { player.getName() }));


            final Component deny = Lang.DENY.getComponent(null)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/towns invite " + player.getName() + " deny"))
                    .hoverEvent(Lang.COMMAND_DENY_INVITE_HOVER.getComponent(new String[] { player.getName() }));

            final Component spacer = Component.text(" ");

            target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_INVITE_TARGET_SUCCESS.getComponent(new String[] { cacheManager.getCacheTowns().getTownName(player.getUniqueId()) }))
                    .append(spacer)
                    .append(accept)
                    .append(spacer)
                    .append(deny));

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
        switch (args.length) {
            case 2 -> {
                return StringUtil.copyPartialMatches(args[1], CoreUtil.getOnlinePlayers(), new ArrayList<>());
            }
            case 3 -> {
                return StringUtil.copyPartialMatches(args[2], Arrays.asList("accept", "deny"), new ArrayList<>());
            }
        }
        return new ArrayList<>();
    }
}
