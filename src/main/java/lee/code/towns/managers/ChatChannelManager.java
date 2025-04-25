package lee.code.towns.managers;

import lee.code.colors.ColorAPI;
import lee.code.playerdata.PlayerDataAPI;
import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChatChannel;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import lee.code.towns.utils.ChatVariableUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
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
  private final Pattern playtimePattern = Pattern.compile("\\{playtime\\}");
  private final Pattern kdrPattern = Pattern.compile("\\{kdr\\}");
  private final Pattern itemInHandPattern = Pattern.compile("\\[item\\]");
  private final Pattern shopPattern = Pattern.compile("\\[shop\\]");
  private final Pattern tagPattern = Pattern.compile("@[A-Za-z0-9_]+");
  private final Pattern linkPattern = Pattern.compile("https?://[A-Za-z0-9\\-\\._~:/?\\[\\]@!$&'()*+,;=%#]+");
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
    CacheManager cacheManager = towns.getCacheManager();
    UUID uuid = player.getUniqueId();
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(nameColorPattern, CoreUtil.parseColorComponent(ColorAPI.getNameColor(player.getUniqueId(), player.getName()))));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(namePattern, player.getName()));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(displayNamePattern, player.displayName()));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(townPattern, cacheManager.getCacheTowns().getTargetTownName(uuid)));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(channelPattern, getChatChannelPrefix(uuid)));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(rolePattern, CoreUtil.parseColorComponent(cacheManager.getCacheTowns().getTargetTownRole(uuid))));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(playtimePattern, ChatVariableUtil.getPlaytime(player)));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(kdrPattern, ChatVariableUtil.getKD(player)));
    chatFormat = chatFormat.replaceText(createTextReplacementConfig(messagePattern, parseMessageVariables(player, message)));
    return chatFormat;
  }

  private Component parseMessageVariables(Player player, Component message) {
    if (message == null) return null;
    message = message.replaceText(createTextReplacementConfig(itemInHandPattern, ChatVariableUtil.getHandItemDisplayName(player).hoverEvent(ChatVariableUtil.getHandItemInfo(player))));
    message = message.replaceText(createTextReplacementConfig(shopPattern, ChatVariableUtil.getShopInfo(player)));
    if (!getChatChannel(player.getUniqueId()).equals(ChatChannel.GLOBAL)) return message;

    String tempMessage = PlainTextComponentSerializer.plainText().serialize(message);
    Matcher matcherTag = tagPattern.matcher(tempMessage);
    while (matcherTag.find()) {
      String group = matcherTag.group();
      String target = group.substring(1);
      Player targetPlayer = PlayerDataAPI.getOnlinePlayer(target);
      if (targetPlayer == null) continue;
      String mention = ColorAPI.getColorChar(targetPlayer.getUniqueId()) + group;
      targetPlayer.playSound(targetPlayer, Sound.BLOCK_NOTE_BLOCK_PLING, (float) 0.3, (float) 1);
      message = message.replaceText(createTextReplacementConfig(Pattern.compile(group), CoreUtil.parseColorComponent(mention)));
    }

    Matcher matcherLink = linkPattern.matcher(tempMessage);
    while (matcherLink.find()) {
      String group = matcherLink.group();
      message = message.replaceText(createTextReplacementConfig(Pattern.compile(group, Pattern.LITERAL), Lang.LINK.getComponent(null).hoverEvent(Lang.LINK_HOVER.getComponent(null)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, group))));
    }
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
