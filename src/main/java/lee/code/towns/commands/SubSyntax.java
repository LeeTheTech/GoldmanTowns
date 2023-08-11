package lee.code.towns.commands;

import lee.code.towns.utils.CoreUtil;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public enum SubSyntax {
    COMMAND_ROLE_SET_SYNTAX("&e/town role set &f<player> <role>"),
    COMMAND_ROLE_COLOR_SYNTAX("&e/town role color &f<role> <color>"),
    COMMAND_RENT_PRICE("&e/town rent price &f<value>"),
    COMMAND_RENT_CLAIM("&e/town rent claim &f<confirm/deny>"),
    COMMAND_RENT_UNCLAIM("&e/town rent unclaim &f<confirm/deny>"),
    COMMAND_RENT_TRUST("&e/town rent trust &f<add/remove> <player>"),
    COMMAND_ROLE_REMOVE_SYNTAX("&e/town role remove &f<player>"),
    COMMAND_ROLE_CREATE_SYNTAX("&e/town role create &f<name>"),
    COMMAND_ROLE_DELETE_SYNTAX("&e/town role delete &f<role>"),
    COMMAND_INVITE_OPTION_SYNTAX("&e/town invite &f<player> <accept/deny>"),
    COMMAND_ABANDON_OPTION_SYNTAX("&e/town abandon &f<confirm/deny>"),
    COMMAND_LEAVE_OPTION_SYNTAX("&e/town leave &f<confirm/deny>"),
    ;
    private final String string;

    public Component getComponent() {
        return CoreUtil.parseColorComponent(string);
    }
}
