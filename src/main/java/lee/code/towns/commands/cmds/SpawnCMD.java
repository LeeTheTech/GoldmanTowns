package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnCMD extends SubCommand {

    private final Towns towns;

    public SpawnCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Teleport to your town spawn.";
    }

    @Override
    public String getSyntax() {
        return "&e/towns spawn";
    }

    @Override
    public String getPermission() {
        return "towns.command.spawn";
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
        final CacheManager cacheManager = towns.getCacheManager();
        final UUID uuid = player.getUniqueId();
        if (cacheManager.getCacheTowns().hasTown(uuid) || cacheManager.getCacheTowns().hasJoinedTown(uuid)) {
            final Location townSpawn = cacheManager.getCacheTowns().getTownSpawn(uuid);
            player.teleportAsync(townSpawn).thenAccept(result -> {
                if (result) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_SPAWN_SUCCESS.getComponent(null)));
                else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_SPAWN_FAILED.getComponent(null)));
            });
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
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
