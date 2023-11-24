package lee.code.towns.listeners.chunks;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import lee.code.towns.Towns;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.MobSpawningEvent;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class MonsterSpawningListener implements Listener {
  private final Towns towns;

  public MonsterSpawningListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onPreCreatureSpawn(PreCreatureSpawnEvent e) {
    if (towns.getData().getMonsterTypes().contains(e.getType())) {
      final MobSpawningEvent mobSpawningEvent = new MobSpawningEvent(e.getSpawnLocation());
      Bukkit.getServer().getPluginManager().callEvent(mobSpawningEvent);
      if (mobSpawningEvent.isCancelled()) e.setCancelled(true);
    }
  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent e) {
    if (towns.getData().getMonsterTypes().contains(e.getEntity().getType())) {
      final MobSpawningEvent mobSpawningEvent = new MobSpawningEvent(e.getLocation());
      Bukkit.getServer().getPluginManager().callEvent(mobSpawningEvent);
      if (mobSpawningEvent.isCancelled()) e.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntitySpawn(EntitySpawnEvent e) {
    if (towns.getData().getMonsterTypes().contains(e.getEntity().getType())) {
      final MobSpawningEvent mobSpawningEvent = new MobSpawningEvent(e.getLocation());
      Bukkit.getServer().getPluginManager().callEvent(mobSpawningEvent);
      if (mobSpawningEvent.isCancelled()) e.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpawnerSpawn(SpawnerSpawnEvent e) {
    if (towns.getData().getMonsterTypes().contains(e.getEntity().getType())) {
      final MobSpawningEvent mobSpawningEvent = new MobSpawningEvent(e.getLocation());
      Bukkit.getServer().getPluginManager().callEvent(mobSpawningEvent);
      if (mobSpawningEvent.isCancelled()) e.setCancelled(true);
    }
  }

  @EventHandler
  public void onMobSpawn(MobSpawningEvent e) {
    e.setCancelled(towns.getCacheManager().checkLocationFlag(ChunkUtil.serializeChunkLocation(e.getLocation().getChunk()), Flag.MONSTER_SPAWNING));
  }
}
