package lee.code.towns.managers;

import lee.code.towns.Towns;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChatChannel;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ChatChannelManager {
    private final Towns towns;
    private final Pattern namePattern = Pattern.compile("\\{name\\}");
    private final Pattern displayNamePattern = Pattern.compile("\\{display-name\\}");
    private final Pattern messagePattern = Pattern.compile("\\{message\\}");
    private final Pattern townPattern = Pattern.compile("\\{town\\}");
    private final Pattern channelPattern = Pattern.compile("\\{channel\\}");
    private final Pattern rolePattern = Pattern.compile("\\{role\\}");
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

    public ChatChannel getChatChannel(UUID uuid) {
        return playerChatChannels.getOrDefault(uuid, ChatChannel.NONE);
    }

    public Component parseChatChannel(Player player, Component chatFormat, Component message) {
        final CacheManager cacheManager = towns.getCacheManager();
        final UUID uuid = player.getUniqueId();
        Component targetMessage = chatFormat;
        targetMessage = targetMessage.replaceText(createTextReplacementConfig(namePattern, player.getName()));
        targetMessage = targetMessage.replaceText(createTextReplacementConfig(displayNamePattern, player.displayName()));
        targetMessage = targetMessage.replaceText(createTextReplacementConfig(messagePattern, message));
        targetMessage = targetMessage.replaceText(createTextReplacementConfig(townPattern, cacheManager.getCacheTowns().getTargetTownName(uuid)));
        targetMessage = targetMessage.replaceText(createTextReplacementConfig(channelPattern, getChatChannelPrefix(uuid)));
        targetMessage = targetMessage.replaceText(createTextReplacementConfig(rolePattern, CoreUtil.parseColorComponent(cacheManager.getCacheTowns().getTargetTownRole(uuid))));
        return targetMessage;
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
