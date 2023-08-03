package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.BorderParticleManager;
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
        return "&e/towns border";
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
        //TODO add options (Rented, Town)
        final CacheManager cacheManager = towns.getCacheManager();
        final UUID uuid = player.getUniqueId();
        if (!cacheManager.getCacheTowns().hasTown(uuid)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
            return;
        }
        final BorderParticleManager borderParticleManager = towns.getBorderParticleManager();
        final boolean active = borderParticleManager.hasBorderActive(uuid);
        if (active) borderParticleManager.stopBorder(uuid);
        else borderParticleManager.scheduleBorder(player);
        final String result = active ? Lang.OFF.getString() : Lang.ON.getString();
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_SUCCESS.getComponent(new String[] { result })));
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
