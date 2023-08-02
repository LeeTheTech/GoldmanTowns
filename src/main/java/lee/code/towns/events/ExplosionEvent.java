package lee.code.towns.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ExplosionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter Location location;
    @Setter @Getter boolean cancelled;

    public ExplosionEvent(Location location) {
        this.location = location;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
