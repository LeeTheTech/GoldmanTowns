package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.PvEEvent;
import lee.code.towns.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class PvEListener implements Listener {

    private final Towns towns;

    public PvEListener(Towns towns) {
        this.towns = towns;
    }

    @EventHandler
    public void onEntityDamageByEntityListener(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player attacker) {
            if (!(e.getEntity() instanceof Player)) {
                final PvEEvent pveEvent = new PvEEvent(attacker, e.getEntity().getLocation());
                Bukkit.getServer().getPluginManager().callEvent(pveEvent);
                if (pveEvent.isCancelled()) e.setCancelled(true);
            }
        } else if (e.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player attacker) {
                final PvEEvent pveEvent = new PvEEvent(attacker, e.getEntity().getLocation());
                Bukkit.getServer().getPluginManager().callEvent(pveEvent);
                if (pveEvent.isCancelled()) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleDestroyListener(VehicleDestroyEvent e) {
        if (e.getAttacker() instanceof Player attacker) {
            final PvEEvent pveEvent = new PvEEvent(attacker, e.getVehicle().getLocation());
            Bukkit.getServer().getPluginManager().callEvent(pveEvent);
            if (pveEvent.isCancelled()) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakByEntityListener(HangingBreakByEntityEvent e) {
        if (e.getRemover() instanceof Player attacker) {
            final PvEEvent pveEvent = new PvEEvent(attacker, e.getEntity().getLocation());
            Bukkit.getServer().getPluginManager().callEvent(pveEvent);
            if (pveEvent.isCancelled()) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvE(PvEEvent e) {
        final CacheManager cacheManager = towns.getCacheManager();
        final boolean result = cacheManager.checkPlayerLocationFlag(e.getAttacker().getUniqueId(), e.getLocation(), Flag.PVE);
        e.setCancelled(result);
        if (result) e.getAttacker().sendActionBar(Lang.ERROR_LOCATION_PERMISSION.getComponent(new String[] { cacheManager.getChunkTownName(e.getLocation()), Flag.PVE.name(), Lang.FALSE.getString() }));
    }
}
