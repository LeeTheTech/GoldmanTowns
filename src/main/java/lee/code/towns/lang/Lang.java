package lee.code.towns.lang;

import lee.code.towns.utils.CoreUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public enum Lang {
    PREFIX("&e&lTowns &6➔ "),
    USAGE("&6&lUsage&7: "),
    ON("&2&lON"),
    OFF("&c&lOFF"),
    TRUE("&2true"),
    FALSE("&cfalse"),
    COMMAND_HELP_DIVIDER("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
    COMMAND_HELP_TITLE("                      &2-== &6&l&nTowns Help&r &2==-"),
    COMMAND_HELP_SUB_COMMAND("&3{0}&b. &e{1}"),
    COMMAND_HELP_SUB_COMMAND_HOVER("&6{0}"),
    COMMAND_CREATE_ANNOUNCEMENT_TOWN_CREATED("&aThe player &6{0} &ahas created the town &3{1}&a!"),
    COMMAND_CREATE_SUCCESS("&aYou successfully created the town &3{0}&a!"),
    COMMAND_BORDER_SUCCESS("&aYou successfully toggled town border {0}&a."),
    COMMAND_CLAIM_SUCCESS("&aYou successfully claimed the chunk &3{0}&a."),
    COMMAND_UNCLAIM_SUCCESS("&aYou successfully unclaimed the chunk &3{0}&a."),
    COMMAND_MAP_HEADER("&a----------- &7[ &2&lMap Key &7] &a-----------"),
    COMMAND_MAP_FOOTER("&a----------------------------------"),
    COMMAND_MAP_LINE_1("    &e\\ {0}&lN &e/ &6&lYOU&7: &9■ &6&lOWNER&7: &2■ &6&lWILD&7: &7■"),
    COMMAND_MAP_LINE_2("    {0}&lW &6&l• {1}&lE &6&lCITIZEN&7: &a■ &6&lCLAIMED&7: &c■"),
    COMMAND_MAP_LINE_3("    &e/ {0}&lS &e\\"),
    COMMAND_SPAWN_SUCCESS("&aYou successfully teleported to your town spawn."),
    COMMAND_SET_SPAWN_SUCCESS("&aYou successfully set your town spawn!"),
    COMMAND_SPAWN_FAILED("&cFailed to teleport to town spawn."),
    COMMAND_AUTO_CLAIM_SUCCESS("&aYou successfully toggled auto claim {0}&a."),
    TELEPORT_CHUNK_SUCCESS("&aYou successfully teleported the chunk &3{0}&a."),
    TELEPORT_CHUNK_FAILED("&cFailed to teleport to chunk &3{0}&c."),
    MENU_FLAG_MANAGER_TITLE("&2&lFlag Manager"),
    MENU_FLAG_MANAGER_CHUNK_TITLE("&2&lChunk Flag Manager"),
    MENU_FLAG_MANAGER_GLOBAL_TITLE("&2&lGlobal Flag Manager"),
    MENU_FLAG_MANAGER_ROLE_TITLE("&2&lRole Flag Manager"),
    MENU_ROLE_SELECTION_MANAGER_TITLE("&2&lRole Selection Manager"),
    MENU_FLAG("&aEnabled&8: {0}"),
    MENU_ROLE_NAME("&e&l{0}"),
    ERROR_LOCATION_PERMISSION("&2&lTown&7: &6{0} &8| &2&l{1}&7: &6{2}"),
    ERROR_NO_PERMISSION("&cYou sadly do not have permission for this."),
    ERROR_NOT_CONSOLE_COMMAND("&cThis command does not work in console."),
    ERROR_ONE_COMMAND_AT_A_TIME("&cYou're currently processing another town command, please wait for it to finish."),
    ERROR_CREATE_HAS_TOWN("&cYou already own a town called &3{0}&c, if you want to create a new town you'll need to disband your current town."),
    ERROR_CREATE_HAS_JOINED_TOWN("&cYou are currently in the town &3{0}&c. You'll need to leave it before creating your own town."),
    ERROR_CREATE_ALREADY_EXIST("&cThe town name &3{0} &chas already been used, you'll need to choose a different name."),
    ERROR_CREATE_CHUNK_CLAIMED("&cThe chunk you're standing on is already owned by &3{0}&c, you need to find a location in the wild to create a town."),
    ERROR_CLAIM_ALREADY_CLAIMED("&cThe chunk &3{0} &cis already owned by the town &3{1}&c."),
    ERROR_CLAIM_NOT_CONNECTED_CHUNK("&cThe chunk &3{0} &cis not connected to a chunk you own already."),
    ERROR_AUTO_CLAIM_NOT_OWNER("&cYou can only toggle on auto claim when you're within your town."),
    ERROR_SET_SPAWN_NOT_CLAIMED("&cYou can only set your town spawn in chunks you own."),
    ERROR_NO_TOWN("&cYou're currently not apart of a town so you can't run this command."),
    ERROR_NOT_TOWN_OWNER("&cYou need to be the town owner to run this command."),
    ERROR_UNCLAIM_NOT_CLAIMED("&cThe chunk &3{0} &cis not claimed."),
    ERROR_UNCLAIM_NOT_OWNER("&cYou're not the owner of chunk &3{0} &cso you can't unclaim it."),
    ERROR_AUTO_CLAIM_RANGE("&cYou are now out of range from your last auto claim, auto claim has been toggled {0}&c."),
    ERROR_FLAG_MANAGER_CHUNK_NOT_CLAIMED("&cThe chunk you're standing on is not claimed by you."),
    ;
    @Getter private final String string;

    public String getString(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return value;
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return value;
    }

    public Component getComponent(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return CoreUtil.parseColorComponent(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return CoreUtil.parseColorComponent(value);
    }
}
