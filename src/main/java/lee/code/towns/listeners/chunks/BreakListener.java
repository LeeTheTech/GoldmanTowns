package lee.code.towns.listeners.chunks;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.Flag;
import lee.code.towns.events.BreakEvent;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakListener implements Listener {

    private final Towns towns;

    public BreakListener(Towns towns) {
        this.towns = towns;
    }

    @EventHandler
    public void onBlockBreakListener(BlockBreakEvent e) {
        final BreakEvent breakEvent = new BreakEvent(e.getPlayer(), e.getBlock().getLocation());
        Bukkit.getServer().getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BreakEvent e) {
        final CacheManager cacheManager = towns.getCacheManager();
        final boolean result = cacheManager.checkPlayerLocationFlag(e.getPlayer().getUniqueId(), e.getLocation(), Flag.BREAK);
        e.setCancelled(result);
        if (result) e.getPlayer().sendActionBar(Lang.ERROR_LOCATION_PERMISSION.getComponent(new String[] { cacheManager.getChunkTownName(e.getLocation()), CoreUtil.capitalize(Flag.BREAK.name()), Lang.FALSE.getString() }));
    }
}
