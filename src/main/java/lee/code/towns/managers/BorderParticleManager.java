package lee.code.towns.managers;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.BorderType;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BorderParticleManager {
  private final Towns towns;
  private final ConcurrentHashMap<UUID, ScheduledTask> borderTaskID = new ConcurrentHashMap<>();

  public BorderParticleManager(Towns towns) {
    this.towns = towns;
  }

  public boolean hasBorderActive(UUID uuid) {
    return borderTaskID.containsKey(uuid);
  }

  private void renderBorderParticlesAroundChunks(Player player, Set<String> chunks) {
    final int playerY = player.getLocation().getBlockY();
    final int maxDistanceChunks = 10;

    for (String chunkStr : chunks) {
      final String[] parts = chunkStr.split(",");
      final World world = Bukkit.getWorld(parts[0]);
      final int chunkX = Integer.parseInt(parts[1]);
      final int chunkZ = Integer.parseInt(parts[2]);

      final Location chunkLocation = new Location(world, (chunkX << 4) + 8, playerY, (chunkZ << 4) + 8);
      final double distance = player.getLocation().distance(chunkLocation) / 16;

      if (distance <= maxDistanceChunks) {

        // check northwest corner
        if (!chunks.contains(String.format("%s,%s,%s", parts[0], chunkX - 1, chunkZ - 1))) {
          spawnParticleLine(player, playerY, new Location(world, (chunkX << 4), playerY, (chunkZ << 4)), new Location(world, (chunkX << 4), playerY, (chunkZ << 4)));
        }

        // check north side
        if (!chunks.contains(String.format("%s,%s,%s", parts[0], chunkX, chunkZ - 1))) {
          spawnParticleLine(player, playerY, new Location(world, (chunkX << 4), playerY, (chunkZ << 4)), new Location(world, (chunkX << 4) + 15, playerY, (chunkZ << 4)));
        }

        // check south side
        if (!chunks.contains(String.format("%s,%s,%s", parts[0], chunkX, chunkZ + 1))) {
          spawnParticleLine(player, playerY, new Location(world, (chunkX << 4), playerY, (chunkZ << 4) + 16), new Location(world, (chunkX << 4) + 15, playerY, (chunkZ << 4) + 16));
        }

        // check west side
        if (!chunks.contains(String.format("%s,%s,%s", parts[0], chunkX - 1, chunkZ))) {
          spawnParticleLine(player, playerY, new Location(world, (chunkX << 4), playerY, (chunkZ << 4)), new Location(world, (chunkX << 4), playerY, (chunkZ << 4) + 15));
        }

        // check east side
        if (!chunks.contains(String.format("%s,%s,%s", parts[0], chunkX + 1, chunkZ))) {
          spawnParticleLine(player, playerY, new Location(world, (chunkX << 4) + 16, playerY, (chunkZ << 4)), new Location(world, (chunkX << 4) + 16, playerY, (chunkZ << 4) + 15));
        }

        // check southeast corner
        if (!chunks.contains(String.format("%s,%s,%s", parts[0], chunkX + 1, chunkZ + 1))) {
          spawnParticleLine(player, playerY, new Location(world, (chunkX << 4) + 16, playerY, (chunkZ << 4) + 16), new Location(world, (chunkX << 4) + 16, playerY, (chunkZ << 4) + 16));
        }
      }
    }
  }

  private void spawnParticleLine(Player player, int y, Location start, Location end) {
    final Particle particle = Particle.VILLAGER_HAPPY;
    final Vector direction = end.toVector().subtract(start.toVector()).normalize();
    final double distance = end.distance(start);

    start.setY(y);
    player.spawnParticle(particle, start, 1);
    for (int p = 0; p < 4; p++) player.spawnParticle(particle, start.add(0, 1, 0), 1);

    for (double i = 1; i < distance; i += 1) {
      final Location location = start.clone().add(direction.clone().multiply(i));
      location.setY(y);
      player.spawnParticle(particle, location, 1);
      for (int p = 0; p < 4; p++) player.spawnParticle(particle, location.add(0, 1, 0), 1);
    }

    end.setY(y);
    player.spawnParticle(particle, end, 1);
    for (int p = 0; p < 4; p++) player.spawnParticle(particle, end.add(0, 1, 0), 1);
  }

  public void spawnParticleChunkBorder(Player player, Chunk chunk, ChunkRenderType type, boolean clientSide) {
    final Particle particle = switch (type) {
      case UNCLAIM -> Particle.FLAME;
      case INFO -> Particle.END_ROD;
      default -> Particle.VILLAGER_HAPPY;
    };
    final Location location = player.getLocation();

    final int minX = chunk.getX() * 16;
    final int minZ = chunk.getZ() * 16;
    final int minY = location.getBlockY();

    final int maxX = minX + 16;
    final int maxZ = minZ + 16;
    final int maxY = minY + 5;

    for (int y = minY; y < maxY; y++) {
      for (int x = minX; x <= maxX; x++) {
        for (int z = minZ; z <= maxZ; z++) {
          if (clientSide) {
            player.spawnParticle(particle, minX, y, z, 0);
            player.spawnParticle(particle, x, y, minZ, 0);
            player.spawnParticle(particle, maxX, y, z, 0);
            player.spawnParticle(particle, x, y, maxZ, 0);
          } else {
            location.getWorld().spawnParticle(particle, minX, y, z, 0);
            location.getWorld().spawnParticle(particle, x, y, minZ, 0);
            location.getWorld().spawnParticle(particle, maxX, y, z, 0);
            location.getWorld().spawnParticle(particle, x, y, maxZ, 0);
          }
        }
      }
    }
  }

  public void scheduleBorder(Player player, BorderType borderType) {
    if (borderTaskID.containsKey(player.getUniqueId())) return;
    borderTaskID.put(player.getUniqueId(), Bukkit.getAsyncScheduler().runAtFixedRate(towns, (scheduledTask) -> {
        final CacheManager cacheManager = towns.getCacheManager();
        final UUID uuid = player.getUniqueId();
        switch (borderType) {
          case TOWN -> {
            if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(uuid)) {
              stopBorder(uuid);
              return;
            }
            final UUID owner = cacheManager.getCacheTowns().getTargetTownOwner(uuid);
            final Set<String> chunks = cacheManager.getCacheChunks().getChunkListData().getChunkList(owner);
            renderBorderParticlesAroundChunks(player, chunks);
          }
          case CHUNK -> {
            final Set<String> chunks = ConcurrentHashMap.newKeySet();
            chunks.add(ChunkUtil.serializeChunkLocation(player.getLocation().getChunk()));
            renderBorderParticlesAroundChunks(player, chunks);
          }
          case RENTED -> {
            if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(uuid)) {
              stopBorder(uuid);
              return;
            }
            final Set<String> chunks = cacheManager.getCacheRenters().getRenterListData().getChunkList(uuid);
            renderBorderParticlesAroundChunks(player, chunks);
          }
        }
      },
      0,
      1,
      TimeUnit.SECONDS
    ));
  }

  public void stopBorder(UUID uuid) {
    borderTaskID.get(uuid).cancel();
    borderTaskID.remove(uuid);
  }
}
