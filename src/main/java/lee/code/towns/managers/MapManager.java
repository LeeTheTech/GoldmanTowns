package lee.code.towns.managers;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MapManager {
  private final Towns towns;
  private final ConcurrentHashMap<UUID, String> autoMapping = new ConcurrentHashMap<>();

  public MapManager(Towns towns){
    this.towns = towns;
  }

  public boolean isAutoMapping(UUID uuid) {
    return autoMapping.containsKey(uuid);
  }

  public void removeAutoMapping(UUID uuid) {
    autoMapping.remove(uuid);
  }

  public void setAutoMapping(UUID uuid, String chunk) {
    autoMapping.put(uuid, chunk);
  }

  public void setLastAutoMapChunkChecked(UUID uuid, String chunk) {
    autoMapping.put(uuid, chunk);
  }

  public String getLastAutoMapChunkChecked(UUID uuid) {
    return autoMapping.get(uuid);
  }

  public void sendMap(Player player, boolean sendKey, int gridSize) {
    final CacheManager cacheManager = towns.getCacheManager();
    final UUID playerID = player.getUniqueId();
    final Chunk chunk = player.getLocation().getChunk();
    final String chunkString = ChunkUtil.serializeChunkLocation(chunk);
    final int playerChunkX = chunk.getX();
    final int playerChunkZ = chunk.getZ();
    final BlockFace playerDirection = CoreUtil.getPlayerFacingDirection(player);
    final ArrayList<Component> lines = new ArrayList<>();
    if (sendKey) {
      final Component spacer = Component.text("");
      lines.add(Lang.COMMAND_MAP_HEADER.getComponent(null));
      lines.add(spacer);
      lines.add(Lang.COMMAND_MAP_LINE_1.getComponent(new String[]{playerDirection.equals(BlockFace.NORTH) ? "&9" : "&b"}));
      lines.add(Lang.COMMAND_MAP_LINE_2.getComponent(new String[]{playerDirection.equals(BlockFace.WEST) ? "&9" : "&b", playerDirection.equals(BlockFace.EAST) ? "&9" : "&b"}));
      lines.add(Lang.COMMAND_MAP_LINE_3.getComponent(new String[]{playerDirection.equals(BlockFace.SOUTH) ? "&9" : "&b"}));
      lines.add(spacer);
      lines.add(Lang.COMMAND_MAP_SPACER.getComponent(null));
    } else {
      lines.add(Lang.COMMAND_AUTO_MAP_HEADER.getComponent(null));
    }
    for (int z = playerChunkZ - (gridSize / 2); z <= playerChunkZ + (gridSize / 2); z++) {
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
          final boolean isCitizen = cacheManager.getCacheTowns().getCitizenData().isCitizen(owner, playerID);
          final boolean isOwner = cacheManager.getCacheChunks().isChunkOwner(targetChunkString, playerID);
          info.append(Lang.COMMAND_MAP_CHUNK_HOVER_TOWN.getString(new String[]{cacheManager.getChunkTownName(targetChunkString)}));
          info.append(Lang.COMMAND_MAP_CHUNK_HOVER_TOWN_OWNER.getString(new String[]{cacheManager.getChunkTownOwnerName(targetChunkString)}));
          if (isOwner) {
            if (!color.equals(NamedTextColor.BLUE)) color = NamedTextColor.DARK_GREEN;
          }
          if (isCitizen) {
            if (!color.equals(NamedTextColor.BLUE)) color = NamedTextColor.GREEN;
          }
          if (cacheManager.getCacheRenters().isRented(targetChunkString)) {
            final UUID renterID = cacheManager.getCacheRenters().getRenter(targetChunkString);
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_RENTED.getString(new String[]{ColorAPI.getNameColor(renterID, cacheManager.getCacheRenters().getRenterName(targetChunkString))}));
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_RENT_COST.getString(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheManager.getCacheRenters().getRentPrice(targetChunkString))})}));
            if (!color.equals(NamedTextColor.BLUE) && renterID.equals(playerID)) color = NamedTextColor.DARK_GREEN;
          }
          if (cacheManager.getCacheRenters().isRentable(targetChunkString)) {
            info.append(Lang.COMMAND_MAP_CHUNK_HOVER_RENT_COST.getString(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheManager.getCacheRenters().getRentPrice(targetChunkString))})}));
            final UUID townOwner = cacheManager.getCacheTowns().getTargetTownOwner(playerID);
            if (!color.equals(NamedTextColor.BLUE) && owner.equals(townOwner)) color = NamedTextColor.YELLOW;
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
    if (!sendKey) lines.add(Lang.COMMAND_AUTO_MAP_SPACER.getComponent(null));
    for (Component line : lines) player.sendMessage(line);
  }
}
