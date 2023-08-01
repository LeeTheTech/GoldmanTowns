package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.BorderParticleManager;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnclaimCMD extends SubCommand {

    private final Towns towns;

    public UnclaimCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "unclaim";
    }

    @Override
    public String getDescription() {
        return "Unclaim the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "&e/towns unclaim";
    }

    @Override
    public String getPermission() {
        return "towns.command.unclaim";
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
        final BorderParticleManager borderParticleManager = towns.getBorderParticleManager();
        final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
        final UUID uuid = player.getUniqueId();
        if (!cacheManager.getCacheChunks().isClaimed(chunk)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_NOT_CLAIMED.getComponent(new String[] { chunk })));
            return;
        }
        if (!cacheManager.getCacheChunks().isChunkOwner(chunk, uuid)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_UNCLAIM_NOT_OWNER.getComponent(new String[] { chunk })));
            return;
        }
        cacheManager.getCacheChunks().unclaimChunk(chunk);
        borderParticleManager.spawnParticleChunkBorder(player.getLocation(), player.getLocation().getChunk(), ChunkRenderType.UNCLAIM);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_UNCLAIM_SUCCESS.getComponent(new String[] { chunk })));
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
