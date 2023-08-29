package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.DamageEvent;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class DamageListener implements Listener {
  private final Towns towns;

  public DamageListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent e) {
    if (towns.getData().getMonsterTypes().contains(e.getEntity().getType())) return;
    if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
    final DamageEvent damageEvent = new DamageEvent(e.getEntity().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(damageEvent);
    if (damageEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onEntityDamageByEntityListener(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player) return;
    if (e.getDamager() instanceof Projectile projectile) {
      if (projectile.getShooter() instanceof Player) return;
    }
    final DamageEvent damageEvent = new DamageEvent(e.getEntity().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(damageEvent);
    if (damageEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onHangingBreakListener(HangingBreakEvent e) {
    final DamageEvent damageEvent = new DamageEvent(e.getEntity().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(damageEvent);
    if (damageEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onHangingBreakByEntityListener(HangingBreakByEntityEvent e) {
    if (e.getRemover() instanceof Player) return;
    final DamageEvent damageEvent = new DamageEvent(e.getEntity().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(damageEvent);
    if (damageEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onVehicleDestroyListener(VehicleDestroyEvent e) {
    if (e.getAttacker() instanceof Player) return;
    final DamageEvent damageEvent = new DamageEvent(e.getVehicle().getLocation());
    Bukkit.getServer().getPluginManager().callEvent(damageEvent);
    if (damageEvent.isCancelled()) e.setCancelled(true);
  }

  @EventHandler
  public void onDamage(DamageEvent e) {
    e.setCancelled(towns.getCacheManager().checkLocationFlag(CoreUtil.serializeLocation(e.getLocation()), Flag.DAMAGE));
  }
}
