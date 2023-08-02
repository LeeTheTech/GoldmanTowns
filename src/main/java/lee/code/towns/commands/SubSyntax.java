package lee.code.towns.commands;

import lee.code.towns.utils.CoreUtil;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public enum SubSyntax {
    COMMAND_ROLE_SET_SYNTAX("&e/town role set &f<player> <role>"),
    COMMAND_ROLE_REMOVE_SYNTAX("&e/town role remove &f<player>"),
    COMMAND_ROLE_CREATE_SYNTAX("&e/town role create &f<name>"),
    COMMAND_INVITE_OPTION_SYNTAX("&e/town invite &f<player> <accept/deny>"),
    COMMAND_ABANDON_OPTION_SYNTAX("&e/town abandon &f<confirm/deny>"),
    ;
    private final String string;

    public Component getComponent() {
        return CoreUtil.parseColorComponent(string);
    }
}
