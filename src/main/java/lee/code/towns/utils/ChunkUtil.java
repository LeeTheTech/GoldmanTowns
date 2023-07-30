package lee.code.towns.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkUtil {

    public static String serializeChunkLocation(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

    public static Location parseChunkLocation(String chunk) {
        final String[] split = chunk.split(",", 3);
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]) * 16, 150, Double.parseDouble(split[2]) * 16, (float) 180.0, (float) 0.0);
    }
}
