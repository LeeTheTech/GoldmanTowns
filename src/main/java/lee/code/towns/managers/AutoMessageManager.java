package lee.code.towns.managers;

import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AutoMessageManager {
    
    private final ConcurrentHashMap<UUID, String> lastChunkChecked = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> lastTownChecked = new ConcurrentHashMap<>();

    public void removeAutoMessageData(UUID uuid) {
        lastChunkChecked.remove(uuid);
        lastTownChecked.remove(uuid);
    }

    public void setLastChunkChecked(UUID uuid, String chunk) {
        lastChunkChecked.put(uuid, chunk);
    }

    public boolean isLastChunkChecked(UUID uuid, String chunk) {
        if (!lastChunkChecked.containsKey(uuid)) return false;
        return lastChunkChecked.get(uuid).equals(chunk);
    }

    public boolean isLastTownChecked(UUID uuid, String town) {
        if (!lastTownChecked.containsKey(uuid)) return false;
        return lastTownChecked.get(uuid).equals(town);
    }

    public void setLastTownChecked(UUID uuid, String town) {
        lastTownChecked.put(uuid, town);
    }

    public void removeLastTownChecked(UUID uuid) {
        lastTownChecked.remove(uuid);
    }

    public void sendTownMessage(Player player, String town) {
        player.sendActionBar(Lang.AUTO_MESSAGE_TOWN.getComponent(new String[] { town }));
    }

    public void sendChunkRentableMessage(Player player, double value) {
        player.sendActionBar(Lang.AUTO_MESSAGE_RENTABLE.getComponent(new String[] { Lang.VALUE_FORMAT.getString(new String[] {
                CoreUtil.parseValue(value)
        })}));
    }
}
