package lee.code.towns.lang;

import lee.code.towns.utils.CoreUtil;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public enum Lang {
    PREFIX("&e&lTowns &6➔ "),
    ERROR_NO_PERMISSION("&cYou sadly do not have permission for this."),
    COMMAND_HELP_DIVIDER("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
    COMMAND_HELP_TITLE("                      &2-== &6&l&nTowns Help&r &2==-"),
    COMMAND_HELP_SUB_COMMAND("&3{0}&b. &e{1}"),
    COMMAND_HELP_SUB_COMMAND_HOVER("&6{0}"),
    ;
    private final String string;

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
