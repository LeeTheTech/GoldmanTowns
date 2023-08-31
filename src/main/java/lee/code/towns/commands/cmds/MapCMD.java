package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapCMD extends SubCommand {
  private final Towns towns;

  public MapCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "map";
  }

  @Override
  public String getDescription() {
    return "Displays a chat map view of the chunks around you.";
  }

  @Override
  public String getSyntax() {
    return "/towns map";
  }

  @Override
  public String getPermission() {
    return "towns.command.map";
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
    final Chunk chunk = player.getLocation().getChunk();
    final String chunkString = ChunkUtil.serializeChunkLocation(chunk);
    final int playerChunkX = chunk.getX();
    final int playerChunkZ = chunk.getZ();
    final BlockFace playerDirection = CoreUtil.getPlayerFacingDirection(player);
    final ArrayList<Component> lines = new ArrayList<>();
    final Component spacer = Component.text("");
    lines.add(Lang.COMMAND_MAP_HEADER.getComponent(null));
    lines.add(spacer);
    lines.add(Lang.COMMAND_MAP_LINE_1.getComponent(new String[]{playerDirection.equals(BlockFace.NORTH) ? "&9" : "&b"}));
    lines.add(Lang.COMMAND_MAP_LINE_2.getComponent(new String[]{playerDirection.equals(BlockFace.WEST) ? "&9" : "&b", playerDirection.equals(BlockFace.EAST) ? "&9" : "&b"}));
    lines.add(Lang.COMMAND_MAP_LINE_3.getComponent(new String[]{playerDirection.equals(BlockFace.SOUTH) ? "&9" : "&b"}));
    lines.add(spacer);
    lines.add(Lang.COMMAND_MAP_FOOTER.getComponent(null));
    for (int z = playerChunkZ - 6; z <= playerChunkZ + 6; z++) {
      Component rowBuilder = Component.empty();
      for (int x = playerChunkX - 10; x <= playerChunkX + 10; x++) {
        final String targetChunkString = chunk.getWorld().getName() + "," + x + "," + z;
        final boolean isClaimed = cacheManager.getCacheChunks().isClaimed(targetChunkString);

        final StringBuilder info = new StringBuilder();
        info.append(Lang.COMMAND_MAP_CHUNK_HOVER_CHUNK.getString(new String[]{targetChunkString}));
        NamedTextColor color = NamedTextColor.GRAY;
        if (chunkString.equals(targetChunkString)) {
          final String directionArrow = switch (playerDirection) {
            case NORTH -> "↑";
            case SOUTH -> "↓";
            case WEST -> "←";
            case EAST -> "→";
            default -> "■";
          };
          info.append(Lang.COMMAND_MAP_CHUNK_HOVER_DIRECTION.getString(new String[]{directionArrow}));
          color = NamedTextColor.BLUE;
        }
        if (isClaimed) {
          if (!color.equals(NamedTextColor.BLUE)) color = NamedTextColor.RED;
          final UUID owner = cacheManager.getCacheChunks().getChunkOwner(targetChunkString);
          final boolean isCitizen = cacheManager.getCacheTowns().getCitizenData().isCitizen(owner, uuid);
          final boolean isOwner = cacheManager.getCacheChunks().isChunkOwner(targetChunkString, uuid);
          info.append(Lang.COMMAND_MAP_CHUNK_HOVER_TOWN.getString(new String[]{cacheManager.getChunkTownName(targetChunkString)}));
          if (isOwner) {
            if (!color.equals(NamedTextColor.BLUE)) color = NamedTextColor.DARK_GREEN;
          }
          if (isCitizen) {
            if (!color.equals(NamedTextColor.BLUE)) color = NamedTextColor.GREEN;
          }
          if (cacheManager.getCacheRenters().isRented(targetChunkString)) {
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_RENTED.getString(new String[]{ColorAPI.getNameColor(cacheManager.getCacheRenters().getRenter(targetChunkString), cacheManager.getCacheRenters().getRenterName(targetChunkString))}));
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_RENT_COST.getString(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheManager.getCacheRenters().getRentPrice(targetChunkString))})}));
          }
          if (cacheManager.getCacheRenters().isRentable(targetChunkString)) {
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_RENT_COST.getString(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheManager.getCacheRenters().getRentPrice(targetChunkString))})}));
          }
          if (cacheManager.getCacheChunks().isOutpostChunk(targetChunkString)) {
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_OUTPOST.getString(null));
          }
          if (cacheManager.getCacheChunks().isEstablishedChunk(targetChunkString)) {
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_ESTABLISHED.getString(null));
          }
        } else {
          info.append(Lang.COMMAND_MAP_CHUNK_HOVER_WILD.getString(null));
        }
        final Component chunkSquare = Component.text("■ ").color(color)
          .hoverEvent(CoreUtil.parseColorComponent(info.toString()))
          .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/towns teleport chunk " + targetChunkString));
        rowBuilder = rowBuilder.append(chunkSquare);
      }
      lines.add(rowBuilder);
    }
    for (Component line : lines) player.sendMessage(line);
  }

  @Override
  public void performConsole(CommandSender console, String[] args) {
    console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
  }

  @Override
  public void performSender(CommandSender sender, String[] args) {
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return new ArrayList<>();
  }
}
