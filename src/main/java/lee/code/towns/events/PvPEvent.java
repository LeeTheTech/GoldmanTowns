package lee.code.towns.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PvPEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();

  @Getter Player attacker;
  @Getter Player victim;
  @Getter Location location;
  @Setter @Getter boolean cancelled;

  public PvPEvent(Player attacker, Player victim, Location location) {
    this.attacker = attacker;
    this.victim = victim;
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
