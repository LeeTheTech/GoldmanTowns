package lee.code.towns.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import lee.code.colors.ColorAPI;
import lee.code.towns.lang.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.Objects;

public class ChatVariableUtil {

  @SuppressWarnings("deprecation")
  public static Component getHandItemInfo(Player player) {
    final StringBuilder itemInfo = new StringBuilder();
    final ItemStack itemStack = player.getInventory().getItemInMainHand();
    final ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null) {
      itemInfo.append(CoreUtil.capitalize(itemStack.getType().name()));
      return CoreUtil.parseColorComponent(itemInfo.toString());
    }
    //name
    if (itemStack.getType().equals(Material.PLAYER_HEAD) && !itemMeta.hasDisplayName()) {
      final SkullMeta skull = (SkullMeta) itemMeta;
      final PlayerProfile playerProfile = skull.getPlayerProfile();
      if (playerProfile != null) {
        final String name = playerProfile.getName();
        itemInfo.append("&e").append(name).append("'s Head").append("\n");
      } else {
        itemInfo.append(getItemNameColor(itemStack)).append(CoreUtil.capitalize(itemStack.getType().name())).append("\n");
      }
    } else if (itemMeta.hasDisplayName()) {
      itemInfo.append(itemMeta.getDisplayName()).append("\n");
    } else {
      itemInfo.append(getItemNameColor(itemStack)).append(CoreUtil.capitalize(itemStack.getType().name())).append("\n");
    }
    //lore
    if (itemMeta.hasLore()) {
      for (String lore : Objects.requireNonNull(itemMeta.getLore())) {
        itemInfo.append(lore).append("\n");
      }
    }
    //enchants
    if (itemMeta.hasEnchants()) {
      if (itemMeta.getItemFlags().isEmpty()) {
        for (Map.Entry<Enchantment, Integer> enchantmentInfo : itemMeta.getEnchants().entrySet()) {
          itemInfo.append(getEnchantmentColor(enchantmentInfo.getKey())).append(CoreUtil.capitalize(enchantmentInfo.getKey().getKey().getKey())).append(" ").append(convertToRoman(enchantmentInfo.getValue())).append("\n");
        }
      }
    }
    //stored enchants
    if (itemStack.getType().equals(Material.ENCHANTED_BOOK)) {
      final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
      for (Map.Entry<Enchantment, Integer> enchantmentInfo : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
        itemInfo.append(getEnchantmentColor(enchantmentInfo.getKey())).append(CoreUtil.capitalize(enchantmentInfo.getKey().getKey().getKey())).append(" ").append(convertToRoman(enchantmentInfo.getValue())).append("\n");
      }
    }
    return CoreUtil.parseColorComponent(itemInfo.substring(0, itemInfo.toString().length() - 1));
  }

  public static String getEnchantmentColor(Enchantment enchantment) {
    switch (enchantment.getKey().getKey()) {
      case "binding_curse", "vanishing_curse" -> {
        return "&c";
      }
      default -> {
        return "&7";
      }
    }
  }

  public static Component getHandItemDisplayName(Player player) {
    final ItemStack itemStack = player.getInventory().getItemInMainHand();
    final ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      if (itemStack.getType().equals(Material.PLAYER_HEAD) && !itemMeta.hasDisplayName()) {
        final SkullMeta skull = (SkullMeta) itemMeta;
        final PlayerProfile playerProfile = skull.getPlayerProfile();
        if (playerProfile != null) {
          final String name = playerProfile.getName();
          return CoreUtil.parseColorComponent("&6[").append(CoreUtil.parseColorComponent("&e" + name + "'s Head")).append(CoreUtil.parseColorComponent("&6]"));
        }
      }
      if (itemMeta.hasDisplayName()) {
        return CoreUtil.parseColorComponent("&6[").append(Objects.requireNonNull(itemMeta.displayName())).append(CoreUtil.parseColorComponent("&6]"));
      }
    }
    return CoreUtil.parseColorComponent("&6[").append(CoreUtil.parseColorComponent(getItemNameColor(itemStack) + CoreUtil.capitalize(itemStack.getType().name()))).append(CoreUtil.parseColorComponent("&6]"));
  }

  public static String getItemNameColor(ItemStack itemStack) {
    final boolean isEnchanted = itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants();
    if (isEnchanted) return "&b";
    switch (itemStack.getType()) {
      case ENCHANTED_BOOK, TOTEM_OF_UNDYING, DRAGON_BREATH, HEART_OF_THE_SEA, NETHER_STAR, ELYTRA, DRAGON_HEAD, PLAYER_HEAD, ZOMBIE_HEAD, CREEPER_HEAD, PIGLIN_HEAD -> {
        return "&e";
      }
      case GOLDEN_APPLE, BEACON, END_CRYSTAL, MUSIC_DISC_5, MUSIC_DISC_13, MUSIC_DISC_11, MUSIC_DISC_BLOCKS, MUSIC_DISC_CAT, MUSIC_DISC_CHIRP, MUSIC_DISC_FAR, MUSIC_DISC_MALL, MUSIC_DISC_MELLOHI, MUSIC_DISC_OTHERSIDE, MUSIC_DISC_RELIC, MUSIC_DISC_STAL, MUSIC_DISC_PIGSTEP, MUSIC_DISC_STRAD, MUSIC_DISC_WAIT, MUSIC_DISC_WARD -> {
        return "&b";
      }
      case ENCHANTED_GOLDEN_APPLE, DRAGON_EGG -> {
        return "&d";
      }
      default -> {
        return "&f";
      }
    }
  }

  public static String convertToRoman(int num) {
    if (num > 10) num = 10;
    final String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    return romanNumerals[num - 1];
  }

  public static Component getShopInfo(Player player) {
    final String shop = Lang.CHAT_SHOP_VARIABLE.getString(new String[]{ColorAPI.getColorChar(player.getUniqueId())});
    final String shopInfo = Lang.CHAT_SHOP_VARIABLE_TITLE.getString(new String[]{ColorAPI.getNameColor(player.getUniqueId(), player.getName())}) + "\n" + Lang.CHAT_SHOP_VARIABLE_INFO.getString();
    return CoreUtil.parseColorComponent(shop)
      .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/shop spawn " + player.getName()))
      .hoverEvent(CoreUtil.parseColorComponent(shopInfo));
  }

  public static Component getPlaytime(Player player) {
    final long secondsPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
    final long millisecondsPlayed = secondsPlayed * 1000;
    return CoreUtil.parseColorComponent(CoreUtil.parseTime(millisecondsPlayed));
  }

  public static Component getKD(Player player) {
    final double kills = player.getStatistic(Statistic.PLAYER_KILLS);
    final double deaths = player.getStatistic(Statistic.DEATHS);
    final double kdr = kills / deaths;
    return Lang.KILL_DEATH_RATIO.getComponent(new String[]{String.valueOf(kdr)});
  }
}
