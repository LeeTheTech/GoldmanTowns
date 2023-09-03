package lee.code.towns.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SubSyntax {
  COMMAND_ROLE_SET_SYNTAX("/town role set &f<player> <role>"),
  COMMAND_ROLE_COLOR_SYNTAX("/town role color &f<role> <color>"),
  COMMAND_RENT_PRICE("/town rent price &f<value>"),
  COMMAND_RENT_CLAIM("/town rent claim &f<confirm/deny>"),
  COMMAND_RENT_UNCLAIM("/town rent unclaim &f<confirm/deny>"),
  COMMAND_RENT_TRUST("/town rent trust &f<add/remove> <player>"),
  COMMAND_ROLE_REMOVE_SYNTAX("/town role remove &f<player>"),
  COMMAND_ROLE_CREATE_SYNTAX("/town role create &f<name>"),
  COMMAND_ROLE_DELETE_SYNTAX("/town role delete &f<role>"),
  COMMAND_INVITE_OPTION_SYNTAX("/town invite &f<player> <accept/deny>"),
  COMMAND_ABANDON_OPTION_SYNTAX("/town abandon &f<confirm/deny>"),
  COMMAND_LEAVE_OPTION_SYNTAX("/town leave &f<confirm/deny>"),
  COMMAND_ADMIN_BONUSCLAIMS_SYNTAX("/town admin bonusclaims &f<set/add/remove> <player> <amount>"),
  COMMAND_ADMIN_DELETE_SYNTAX("/town admin delete &f<player>"),

  ;

  @Getter private final String string;
}
