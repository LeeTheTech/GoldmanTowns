package lee.code.towns.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlyEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();

  @Getter Player player;
  @Getter Location location;
  @Getter String chunk;
  @Setter @Getter boolean cancelled;

  public FlyEvent(Player player, Location location, String chunk) {
    this.player = player;
    this.location = location;
    this.chunk = chunk;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
