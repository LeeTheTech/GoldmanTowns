package lee.code.towns.utils;

import lee.code.towns.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChunkUtil {
  public static String serializeChunkLocation(Chunk chunk) {
    return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
  }

  public static Location parseChunkLocation(String chunk) {
    final String[] split = chunk.split(",", 3);
    return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]) * 16, 150, Double.parseDouble(split[2]) * 16, (float) 180.0, (float) 0.0);
  }

  public static void teleportToMiddleOfChunk(Player player, String chunkString) {
    final Location location = parseChunkLocation(chunkString);
    final World world = location.getWorld();

    world.getChunkAtAsync(location).thenAccept(chunk -> {
      final int chunkX = chunk.getX();
      final int chunkZ = chunk.getZ();
      final int middleX = (chunkX << 4) + 8;
      final int middleZ = (chunkZ << 4) + 8;

      final double yLevel = world.getHighestBlockYAt(middleX, middleZ) + 1;

      final Location teleportLocation = new Location(world, middleX + 0.5, yLevel, middleZ + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());
      player.teleportAsync(teleportLocation).thenAccept(result -> {
        if (result)
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.TELEPORT_CHUNK_SUCCESS.getComponent(new String[]{chunkString})));
        else
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.TELEPORT_CHUNK_FAILED.getComponent(new String[]{chunkString})));
      });
    });
  }
}
