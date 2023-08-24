package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.BorderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.BorderParticleManager;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BorderCMD extends SubCommand {

    private final Towns towns;

    public BorderCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "border";
    }

    @Override
    public String getDescription() {
        return "Toggle town border to view particles around claimed chunks.";
    }

    @Override
    public String getSyntax() {
        return "&e/towns border &f<town, rented, chunk, off>";
    }

    @Override
    public String getPermission() {
        return "towns.command.border";
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
        if (args.length <= 1) {
            player.sendMessage(Lang.USAGE.getComponent(null).append(CoreUtil.parseColorComponent(getSyntax())));
            return;
        }
        final String option = args[1].toLowerCase();
        final CacheManager cacheManager = towns.getCacheManager();
        final UUID uuid = player.getUniqueId();
        final BorderParticleManager borderParticleManager = towns.getBorderParticleManager();
        switch (option) {
            case "town" -> {
                if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(uuid)) {
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
                    return;
                }
                if (borderParticleManager.hasBorderActive(uuid)) borderParticleManager.stopBorder(uuid);
                borderParticleManager.scheduleBorder(player, BorderType.valueOf(option.toUpperCase()));
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_SUCCESS.getComponent(new String[] { option })));
            }
            case "chunk" -> {
                if (borderParticleManager.hasBorderActive(uuid)) borderParticleManager.stopBorder(uuid);
                borderParticleManager.scheduleBorder(player, BorderType.valueOf(option.toUpperCase()));
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_SUCCESS.getComponent(new String[] { option })));
            }
            case "rented" -> {
                if (!cacheManager.getCacheRenters().getRenterListData().hasRentedChunks(uuid)) {
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BORDER_NONE_RENTED.getComponent(null)));
                    return;
                }
                if (borderParticleManager.hasBorderActive(uuid)) borderParticleManager.stopBorder(uuid);
                borderParticleManager.scheduleBorder(player, BorderType.valueOf(option.toUpperCase()));
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_SUCCESS.getComponent(new String[] { option })));
            }
            case "off" -> {
                if (borderParticleManager.hasBorderActive(uuid)) borderParticleManager.stopBorder(uuid);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_OFF_SUCCESS.getComponent(new String[] { Lang.OFF.getString(null) } )));
            }
            default -> player.sendMessage(Lang.USAGE.getComponent(null).append(CoreUtil.parseColorComponent(getSyntax())));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
    }

    @Override
    public void performSender(CommandSender sender, String[] args) { }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
