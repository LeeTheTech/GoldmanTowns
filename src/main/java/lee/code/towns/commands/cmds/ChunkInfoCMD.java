package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChunkInfoCMD extends SubCommand {

    private final Towns towns;

    public ChunkInfoCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "chunkinfo";
    }

    @Override
    public String getDescription() {
        return "Info about the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "&e/towns chunkinfo";
    }

    @Override
    public String getPermission() {
        return "towns.command.chunkinfo";
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
        final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
        if (cacheManager.getCacheChunks().isClaimed(chunk)) {
            final ArrayList<Component> lines = new ArrayList<>();
            lines.add(Lang.COMMAND_CHUNK_INFO_HEADER.getComponent(null));
            lines.add(Component.text(""));
            lines.add(Lang.COMMAND_CHUNK_INFO_CHUNK.getComponent(new String[] { chunk }));
            lines.add(Lang.COMMAND_CHUNK_INFO_TOWN_OWNER.getComponent(new String[] { cacheManager.getChunkTownName(chunk) }));
            if (cacheManager.getCacheRenters().isRented(chunk)) {
                lines.add(Lang.COMMAND_CHUNK_INFO_RENTER.getComponent(new String[] { cacheManager.getCacheRenters().getRenterName(chunk) }));
                lines.add(Lang.COMMAND_CHUNK_INFO_RENT_COST.getComponent(new String[] {
                        Lang.VALUE_FORMAT.getString(new String[] { CoreUtil.parseValue(cacheManager.getCacheRenters().getRentPrice(chunk)) })
                }));
            }
            if (cacheManager.getCacheRenters().isRentable(chunk)) {
                lines.add(Lang.COMMAND_CHUNK_INFO_RENT_COST.getComponent(new String[] {
                        Lang.VALUE_FORMAT.getString(new String[] { CoreUtil.parseValue(cacheManager.getCacheRenters().getRentPrice(chunk)) })
                }));
            }
            if (cacheManager.getCacheChunks().isEstablishedChunk(chunk)) {
                lines.add(Lang.COMMAND_CHUNK_INFO_TOWN_ESTABLISHED_CHUNK.getComponent(new String[] { Lang.TRUE.getString() }));
            }
            lines.add(Component.text(""));
            lines.add(Lang.COMMAND_CHUNK_INFO_FOOTER.getComponent(null));
            for (Component line : lines) player.sendMessage(line);
            towns.getBorderParticleManager().spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.INFO, true);
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_CHUNK_INFO_NOT_CLAIMED.getComponent(null)));
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
