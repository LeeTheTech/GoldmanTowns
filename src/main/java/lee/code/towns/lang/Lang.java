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
    COMMAND_HELP_DIVIDER("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
    COMMAND_HELP_TITLE("                      &2-== &6&l&nTowns Help&r &2==-"),
    COMMAND_HELP_SUB_COMMAND("&3{0}&b. &e{1}"),
    COMMAND_HELP_SUB_COMMAND_HOVER("&6{0}"),
    COMMAND_CREATE_ANNOUNCEMENT_TOWN_CREATED("&aThe player &6{0} &ahas created the town &3{1}&a!"),
    COMMAND_CREATE_SUCCESS("&aYou successfully created the town &3{0}&a!"),
    COMMAND_BORDER_SUCCESS("&aYou successfully toggled town border {0}&a."),
    COMMAND_CLAIM_SUCCESS("&aYou successfully claimed the chunk &3{0}&a."),
    ERROR_NO_PERMISSION("&cYou sadly do not have permission for this."),
    ERROR_NOT_CONSOLE_COMMAND("&cThis command does not work in console."),
    ERROR_ONE_COMMAND_AT_A_TIME("&cYou're currently processing another town command, please wait for it to finish."),
    ERROR_CREATE_HAS_TOWN("&cYou already own a town called &3{0}&c, if you want to create a new town you'll need to disband your current town."),
    ERROR_CREATE_HAS_JOINED_TOWN("&cYou are currently in the town &3{0}&c. You'll need to leave it before creating your own town."),
    ERROR_CREATE_ALREADY_EXIST("&cThe town name &3{0} &chas already been used, you'll need to choose a different name."),
    ERROR_CREATE_CHUNK_CLAIMED("&cThe chunk you're standing on is already owned by &3{0}&c, you need to find a location in the wild to create a town."),
    ERROR_CLAIM_ALREADY_CLAIMED("&cThe chunk &3{0} &cis already owned by the town &3{1}&c."),
    ERROR_CLAIM_NOT_CONNECTED_CHUNK("&cThe chunk &3{0} &cis not connected to a chunk you own already."),
    ERROR_NO_TOWN("&cYou're currently not apart of a town so you can't run this command."),
    ERROR_NOT_TOWN_OWNER("&cYou need to be the town owner to run this command."),
    ;
    @Getter private final String string;

    public String getString(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return CoreUtil.parseColorString(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return CoreUtil.parseColorString(value);
    }

    public Component getComponent(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return CoreUtil.parseColorComponent(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return CoreUtil.parseColorComponent(value);
    }
}
