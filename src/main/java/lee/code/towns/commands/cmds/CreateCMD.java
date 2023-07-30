package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateCMD extends SubCommand {

    private final Towns towns;

    public CreateCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a new town.";
    }

    @Override
    public String getSyntax() {
        return "/towns create &f<name>";
    }

    @Override
    public String getPermission() {
        return "towns.command.create";
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
        final UUID uuid = player.getUniqueId();
        final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
        final CacheManager cacheManager = towns.getCacheManager();
        if (args.length > 1) {
            if (cacheManager.getCachePlayers().hasTown(uuid)) {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_HAS_TOWN.getComponent(new String[] { cacheManager.getCachePlayers().getTown(uuid) })));
                return;
            }
            if (cacheManager.getCachePlayers().hasJoinedTown(uuid)) {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_HAS_JOINED_TOWN.getComponent(new String[] { cacheManager.getCachePlayers().getJoinedTown(uuid) })));
                return;
            }
            final String town = CoreUtil.removeSpecialCharacters(CoreUtil.buildStringFromArgs(args, 1));
            if (cacheManager.getCachePlayers().isTownTaken(town)) {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_ALREADY_EXIST.getComponent(new String[] { town })));
                return;
            }
            if (cacheManager.getCacheChunks().isClaimed(chunk)) {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CREATE_CHUNK_CLAIMED.getComponent(new String[] { town })));
                return;
            }
            cacheManager.getCacheChunks().claim(chunk, uuid);
            cacheManager.getCachePlayers().setTown(uuid, town, player.getLocation());
            towns.getBorderParticleManager().spawnParticleChunkBorder(player.getLocation(), player.getLocation().getChunk(), ChunkRenderType.CLAIM);
            Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CREATE_ANNOUNCEMENT_TOWN_CREATED.getComponent(new String[] { player.getName(), town })));
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_CREATE_SUCCESS.getComponent(new String[] { town })));
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
        return new ArrayList<>();
    }
}
