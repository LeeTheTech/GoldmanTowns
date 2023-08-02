package lee.code.towns.menus.menu.menudata;

import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum MenuItem {

    FILLER_GLASS(Material.BLACK_STAINED_GLASS_PANE, "", null, false, false, null, null),
    FLAG_MANAGER_CHUNK(Material.PLAYER_HEAD, "&2&lChunk Flag Manager", "&7Flags for the chunk you're standing on, these\n&7will override global flags.", false, false, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZiYjlmYjk3YmE4N2NiNzI3Y2QwZmY0NzdmNzY5MzcwYmVhMTljY2JmYWZiNTgxNjI5Y2Q1NjM5ZjJmZWMyYiJ9fX0=", MenuRout.FLAG_MANAGER_CHUNK),
    FLAG_MANAGER_GLOBAL(Material.PLAYER_HEAD, "&2&lGlobal Flag Manager", "&7Flags for all town chunks, these include\n&7players not apart of your town.", false, false, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUyY2M0MjAxNWU2Njc4ZjhmZDQ5Y2NjMDFmYmY3ODdmMWJhMmMzMmJjZjU1OWEwMTUzMzJmYzVkYjUwIn19fQ==", MenuRout.FLAG_MANAGER_GLOBAL),
    FLAG_MANAGER_ROLE(Material.PLAYER_HEAD, "&2&lRole Flag Manager", "&7Flags for each individual town role, these\n&7will override global and chunk flags.", false, false, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkNDliYWU5NWM3OTBjM2IxZmY1YjJmMDEwNTJhNzE0ZDYxODU0ODFkNWIxYzg1OTMwYjNmOTlkMjMyMTY3NCJ9fX0=", MenuRout.ROLE_SELECTION_MANAGER),
    BACK(Material.BARRIER, "&c&l<-- Back", null, false, false, null, null),
    ROLE(Material.NAME_TAG, "", null, false, false, null, null)

    ;
    private final Material material;
    private final String name;
    private final String lore;
    private final boolean hideItemFlags;
    private final boolean enchantItem;
    private final String skin;
    @Getter private final MenuRout menuRout;

    public ItemStack createItem() {
        final ItemStack item = ItemUtil.createItem(material, name, lore, 0, skin);
        if (hideItemFlags) ItemUtil.hideItemFlags(item);
        if (enchantItem) ItemUtil.enchantItem(item, Enchantment.ARROW_INFINITE, 1);
        return item;
    }

    public ItemStack createRoleItem(String role) {
        return ItemUtil.createItem(material, Lang.MENU_ROLE_NAME.getString(new String[] { role }), lore, 0, skin);
    }

}
