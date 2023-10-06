package lee.code.towns.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ItemUtil {

  public static ItemStack createItem(Material material, String name, String lore, int modelData, String skin) {
    final ItemStack item = new ItemStack(material);
    final ItemMeta itemMeta = item.getItemMeta();
    if (itemMeta == null) return item;
    if (skin != null) applyHeadSkin(itemMeta, skin);
    if (lore != null) setItemLore(itemMeta, lore);
    if (name != null) itemMeta.displayName(CoreUtil.parseColorComponent(name));
    if (modelData != 0) itemMeta.setCustomModelData(modelData);
    item.setItemMeta(itemMeta);
    return item;
  }

  public static void applyHeadSkin(ItemMeta itemMeta, String base64) {
    try {
      final SkullMeta skullMeta = (SkullMeta) itemMeta;
      final GameProfile profile = new GameProfile(UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"), null);
      profile.getProperties().put("textures", new Property("textures", base64));
      if (skullMeta != null) {
        final Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
        mtd.setAccessible(true);
        mtd.invoke(skullMeta, profile);
      }
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  public static void setItemLore(ItemMeta itemMeta, String lore) {
    if (itemMeta == null) return;
    final String[] split = StringUtils.split(lore, "\n");
    final List<Component> pLines = new ArrayList<>();
    for (String line : split) pLines.add(CoreUtil.parseColorComponent(line));
    itemMeta.lore(pLines);
  }

  public static void hideItemFlags(ItemStack itemStack) {
    final ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null) return;
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    itemStack.setItemMeta(itemMeta);
  }

  public static void enchantItem(ItemStack itemStack, Enchantment enchantment, int level) {
    final ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null) return;
    itemMeta.addEnchant(enchantment, level, false);
    itemStack.setItemMeta(itemMeta);
  }

  public static String serializeItemStack(ItemStack item) {
    try {
      final ByteArrayOutputStream io = new ByteArrayOutputStream();
      final BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
      os.writeObject(item);
      os.flush();
      final byte[] serializedObject = io.toByteArray();
      return Base64.getEncoder().encodeToString(serializedObject);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static ItemStack parseItemStack(String serializedItemStack) {
    try {
      final byte[] serializedObject = Base64.getDecoder().decode(serializedItemStack);
      final ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
      final BukkitObjectInputStream is = new BukkitObjectInputStream(in);
      return (ItemStack) is.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static int getFreeSpace(Player player, ItemStack item) {
    int freeSpaceCount = 0;
    for (int slot = 0; slot <= 35; slot++) {
      final ItemStack slotItem = player.getInventory().getItem(slot);
      if (slotItem == null || slotItem.getType() == Material.AIR) {
        freeSpaceCount += item.getMaxStackSize();
      } else if (slotItem.isSimilar(item))
        freeSpaceCount += Math.max(0, slotItem.getMaxStackSize() - slotItem.getAmount());
    }
    return freeSpaceCount;
  }

  public static boolean canReceiveItems(Player player, ItemStack item, int amount) {
    return getFreeSpace(player, item) >= amount;
  }

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
    if (itemMeta.hasDisplayName()) itemInfo.append(itemMeta.getDisplayName()).append("\n");
    else itemInfo.append(getItemNameColor(itemStack)).append(CoreUtil.capitalize(itemStack.getType().name())).append("\n");
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
    if (itemMeta.hasDisplayName()) {
      return CoreUtil.parseColorComponent("&6[").append(Objects.requireNonNull(itemMeta.displayName())).append(CoreUtil.parseColorComponent("&6]"));
    } else {
      return CoreUtil.parseColorComponent("&6[").append(CoreUtil.parseColorComponent(getItemNameColor(itemStack) + CoreUtil.capitalize(itemStack.getType().name()))).append(CoreUtil.parseColorComponent("&6]"));
    }
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
    final String[] romanNumerals = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
    return romanNumerals[num - 1];
  }
}
