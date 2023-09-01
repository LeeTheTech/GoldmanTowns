package lee.code.towns.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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
}
