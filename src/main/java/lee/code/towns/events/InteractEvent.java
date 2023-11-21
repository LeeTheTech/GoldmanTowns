package lee.code.towns.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InteractEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  @Getter Player player;
  @Getter Location location;
  @Getter Block block;
  @Setter @Getter boolean cancelled;

  public InteractEvent(Player player, Location location, Block block) {
    this.player = player;
    this.location = location;
    this.block = block;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
