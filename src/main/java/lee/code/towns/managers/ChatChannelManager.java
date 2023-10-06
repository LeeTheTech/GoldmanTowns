package lee.code.towns.managers;

import lee.code.colors.ColorAPI;
import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChatChannel;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import lee.code.towns.utils.ChatVariableUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ChatChannelManager {
  private final Towns towns;
  private final Pattern nameColorPattern = Pattern.compile("\\{color-name\\}");
  private final Pattern namePattern = Pattern.compile("\\{name\\}");
  private final Pattern displayNamePattern = Pattern.compile("\\{display-name\\}");
  private final Pattern messagePattern = Pattern.compile("\\{message\\}");
  private final Pattern townPattern = Pattern.compile("\\{town\\}");
  private final Pattern channelPattern = Pattern.compile("\\{channel\\}");
  private final Pattern rolePattern = Pattern.compile("\\{role\\}");
  private final Pattern itemInHandPattern = Pattern.compile("\\[item\\]");
  private final Pattern shopPattern = Pattern.compile("\\[shop\\]");
  private final ConcurrentHashMap<UUID, ChatChannel> playerChatChannels = new ConcurrentHashMap<>();

  public ChatChannelManager(Towns towns) {
    this.towns = towns;
  }

  public void setChatChannel(UUID uuid, ChatChannel chatChannel) {
    playerChatChannels.put(uuid, chatChannel);
  }

  public void removeChatChannel(UUID uuid) {
    playerChatChannels.remove(uuid);
  }

  public boolean hasChatChannelData(UUID uuid) {
    return playerChatChannels.containsKey(uuid);
  }

  public ChatChannel getChatChannel(UUID uuid) {
    return playerChatChannels.getOrDefault(uuid, ChatChannel.GLOBAL);
  }

  public Component parseMessage(Player player, Component chatFormat, Component message) {
    return setClick(player, setHover(player, parseChatVariables(player, chatFormat, message)));
  }

  private Component parseChatVariables(Player player, Component chatFormat, Component message) {
    final CacheManager cacheManager = towns.getCacheManager();
    final UUID uuid = player.getUniqueId();
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(nameColorPattern, CoreUtil.parseColorComponent(ColorAPI.getNameColor(player.getUniqueId(), player.getName()))));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(namePattern, player.getName()));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(displayNamePattern, player.displayName()));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(messagePattern, parseMessageVariables(player, message)));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(townPattern, cacheManager.getCacheTowns().getTargetTownName(uuid)));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(channelPattern, getChatChannelPrefix(uuid)));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(rolePattern, CoreUtil.parseColorComponent(cacheManager.getCacheTowns().getTargetTownRole(uuid))));
    return chatFormat;
  }

  private Component parseMessageVariables(Player player, Component message) {
    if (message == null) return null;
    message = message.replaceText(createTextReplacementConfig(itemInHandPattern, ChatVariableUtil.getHandItemDisplayName(player).hoverEvent(ChatVariableUtil.getHandItemInfo(player))));
    message = message.replaceText(createTextReplacementConfig(shopPattern, ChatVariableUtil.getShopInfo(player)));
    return message;
  }

  private Component setHover(Player player, Component message) {
    return message.hoverEvent(parseChatVariables(player, Lang.MESSAGE_HOVER.getComponent(null), null));
  }

  private Component setClick(Player player, Component message) {
    return message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));
  }

  private Component getChatChannelPrefix(UUID uuid) {
    switch (getChatChannel(uuid)) {
      case TOWN -> {
        return Lang.CHAT_CHANNEL_TOWN_PREFIX.getComponent(null);
      }
      default -> {
        return Lang.CHAT_CHANNEL_GLOBAL_PREFIX.getComponent(null);
      }
    }
  }

  private TextReplacementConfig createTextReplacementConfig(Pattern pattern, String message) {
    return TextReplacementConfig.builder().match(pattern).replacement(message).build();
  }

  private TextReplacementConfig createTextReplacementConfig(Pattern pattern, Component message) {
    return TextReplacementConfig.builder().match(pattern).replacement(message).build();
  }
}
