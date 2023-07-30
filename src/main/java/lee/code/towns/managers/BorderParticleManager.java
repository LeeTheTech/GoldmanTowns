package lee.code.towns.managers;

import lee.code.towns.Towns;
import lee.code.towns.enums.ChunkRenderType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BorderParticleManager {
    private final Towns towns;

    public BorderParticleManager(Towns towns) {
        this.towns = towns;
    }

    private final ConcurrentHashMap<UUID, Integer> borderTaskID = new ConcurrentHashMap<>();

    public boolean hasBorderActive(UUID uuid) {
        return borderTaskID.containsKey(uuid);
    }

    private void renderBorderParticlesAroundChunks(Player player, HashSet<String> chunks) {
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

    public void spawnParticleChunkBorder(Location location, Chunk chunk, ChunkRenderType type) {
        final Particle particle = switch (type) {
            case UNCLAIM -> Particle.FLAME;
            case INFO -> Particle.END_ROD;
            default -> Particle.VILLAGER_HAPPY;
        };

        final long minX = chunk.getX() * 16L;
        final long minZ = chunk.getZ() * 16L;
        final long minY = location.getBlockY();

        final long maxX = minX + 16;
        final long maxZ = minZ + 16;
        final long maxY = minY + 5;

        for (long y = minY; y < maxY; y++) {
            for (long x = minX; x <= maxX; x++) {
                for (long z = minZ; z <= maxZ; z++) {
                    location.getWorld().spawnParticle(particle, minX, y, z, 0);
                    location.getWorld().spawnParticle(particle, x, y, minZ, 0);
                    location.getWorld().spawnParticle(particle, maxX, y, z, 0);
                    location.getWorld().spawnParticle(particle, x, y, maxZ, 0);
                }
            }
        }
    }

    public void scheduleBorder(Player player) {
        if (borderTaskID.containsKey(player.getUniqueId())) return;
        final int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(towns, () -> Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
            final HashSet<String> chunks = towns.getCacheManager().getCacheChunks().getChunkList(player.getUniqueId());
            renderBorderParticlesAroundChunks(player, chunks);
        }), 0L, 20L);
        borderTaskID.put(player.getUniqueId(), taskID);
    }

    public void stopBorder(UUID uuid) {
        Bukkit.getScheduler().cancelTask(borderTaskID.get(uuid));
        borderTaskID.remove(uuid);
    }
}
