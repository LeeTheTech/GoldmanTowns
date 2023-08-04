package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.managers.BorderParticleManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbandonCMD extends SubCommand {

    private final Towns towns;

    public AbandonCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "abandon";
    }

    @Override
    public String getDescription() {
        return "Abandon your town, this will completely delete your town.";
    }

    @Override
    public String getSyntax() {
        return "&e/towns abandon &f<confirm/deny>";
    }

    @Override
    public String getPermission() {
        return "towns.command.abandon";
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
        final CacheManager cacheManager = towns.getCacheManager();
        final BorderParticleManager borderParticleManager = towns.getBorderParticleManager();
        final AutoClaimManager autoClaimManager = towns.getAutoClaimManager();
        final UUID uuid = player.getUniqueId();
        if (!cacheManager.getCacheTowns().hasTown(uuid)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
            return;
        }
        if (borderParticleManager.hasBorderActive(uuid)) borderParticleManager.stopBorder(uuid);
        if (autoClaimManager.isAutoClaiming(uuid)) autoClaimManager.removeAutoClaiming(uuid);
        final String town = cacheManager.getCacheTowns().getTownName(uuid);
//        if (args.length > 1) {
//            switch (args[1].toLowerCase()) {
//                case "confirm" -> {
//                    cacheManager.deleteTown(uuid);
//                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDON_SUCCESS.getComponent(new String[] { town })));
//                }
//                case "deny"-> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDON_DENY.getComponent(null)));
//                default -> player.sendMessage(Lang.USAGE.getComponent(null).append(SubSyntax.COMMAND_ABANDON_OPTION_SYNTAX.getComponent()));
//            }
//        } else {
//            final TextComponent accept = FileLang.CONFIRM.getTextComponent(null);
//            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/towns abandon confirm"));
//            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(FileLang.CONFIRM_ABANDON_HOVER.getString(null))));
//
//            final TextComponent deny = FileLang.DENY.getTextComponent(null);
//            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/towns abandon deny"));
//            deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(FileLang.DENY_ABANDON_HOVER.getString(new String[] { player.getName() }))));
//
//            final TextComponent spacer = new TextComponent(" ");
//
//            player.spigot().sendMessage(
//                    FileLang.PREFIX.getTextComponent(null),
//                    FileLang.COMMAND_ABANDON_WARNING.getTextComponent(new String[] { cacheManager.getTown(player.getUniqueId()) }),
//                    spacer,
//                    accept,
//                    spacer,
//                    deny
//            );
//        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        //console.sendMessage(FileLang.PREFIX.getString(null) + FileLang.ERROR_NOT_CONSOLE_COMMAND.getString(null));
    }

    @Override
    public void performSender(CommandSender sender, String[] args) { }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
