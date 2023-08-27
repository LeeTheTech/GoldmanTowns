package lee.code.towns.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChatChannel;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.ChatChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ChatListener implements Listener {
  private final Towns towns;

  public ChatListener(Towns towns) {
    this.towns = towns;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onAsyncPlayerChatListener(AsyncChatEvent e) {
    if (e.isCancelled()) return;
    e.setCancelled(true);
    final CacheManager cacheManager = towns.getCacheManager();
    final ChatChannelManager chat = towns.getChatChannelManager();
    final Player player = e.getPlayer();
    final UUID uuid = player.getUniqueId();
    if (chat.hasChatChannelData(uuid) && !cacheManager.getCacheTowns().hasTownOrJoinedTown(uuid)) {
      chat.setChatChannel(uuid, ChatChannel.GLOBAL);
    }
    switch (chat.getChatChannel(uuid)) {
      case GLOBAL ->
        Bukkit.getServer().sendMessage(chat.parseMessage(player, Lang.CHAT_GLOBAL.getComponent(null), e.message()));
      case TOWN ->
        cacheManager.getCacheTowns().sendTownMessage(uuid, chat.parseMessage(player, Lang.CHAT_TOWN.getComponent(null), e.message()));
    }
  }
}
