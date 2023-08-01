package lee.code.towns.menus.menu.enums;

import lee.code.towns.enums.Flag;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum FlagMenuItem {

    INVITE("&e&lInvite", "&7Able to invite players\n&7to the town.", Flag.INVITE),
    BUILD("&e&lBuild", "&7Anything that can be built by\n&7a player or mob.", Flag.BUILD),
    CHUNK_FLAGS_ENABLED("&e&lChunk Flags Enabled", "&7Should chunk flags be enabled.\n&7When enabled they override global\n&7flags set.", Flag.CHUNK_FLAGS_ENABLED),
    BREAK("&e&lBreak", "&7Anything that can be broken by\n&7a player or mob.", Flag.BREAK),
    INTERACT("&e&lInteract", "&7Anything that can be interacted\n&7with by a player or mob.", Flag.INTERACT),
    DAMAGE("&e&lDamage", "&7Anything that inflicts damage\n&7that is not from a player.", Flag.DAMAGE),
    PVP("&e&lPvP", "&7Damage taken or inflicted by a player.", Flag.PVP),
    PVE("&e&lPvE", "&7Damage taken or inflicted by a mob.", Flag.PVE),
    MONSTER_SPAWNING("&e&lMonster Spawning", "&7Spawn hostile monsters.", Flag.MONSTER_SPAWNING),
    REDSTONE("&e&lRedstone", "&7Redstone usage.", Flag.REDSTONE),
    EXPLOSION("&e&lExplosions", "&7Explosions from mobs or players.", Flag.EXPLOSION),
    TELEPORT("&e&lTeleport", "&7Ender pearl or chorus fruit usage.", Flag.TELEPORT),

    ;

    private final String name;
    private final String lore;
    @Getter private final Flag flag;

    public ItemStack createItem(boolean result) {
        final Material material = result ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        final String enabled = result ? Lang.TRUE.getString() : Lang.FALSE.getString();
        return ItemUtil.createItem(material, name, lore + "\n \n" + Lang.MENU_FLAG.getString(new String[] { enabled }), 0, null);
    }

}
