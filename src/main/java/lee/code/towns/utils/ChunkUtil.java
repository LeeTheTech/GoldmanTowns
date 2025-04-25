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
    String[] split = chunk.split(",", 3);
    return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]) * 16, 150, Double.parseDouble(split[2]) * 16, (float) 180.0, (float) 0.0);
  }

  public static void teleportToMiddleOfChunk(Player player, String chunkString) {
    Location location = parseChunkLocation(chunkString);
    World world = location.getWorld();

    world.getChunkAtAsync(location).thenAccept(chunk -> {
      int chunkX = chunk.getX();
      int chunkZ = chunk.getZ();
      int middleX = (chunkX << 4) + 8;
      int middleZ = (chunkZ << 4) + 8;

      double yLevel = world.getHighestBlockYAt(middleX, middleZ) + 1;

      Location teleportLocation = new Location(world, middleX + 0.5, yLevel, middleZ + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());
      player.teleportAsync(teleportLocation).thenAccept(result -> {
        if (result) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.TELEPORT_CHUNK_SUCCESS.getComponent(new String[]{chunkString})));
        else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.TELEPORT_CHUNK_FAILED.getComponent(new String[]{chunkString})));
      });
    });
  }
}
