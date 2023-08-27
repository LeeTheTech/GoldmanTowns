package lee.code.towns.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Color {
  BLACK("&0"),
  DARK_BLUE("&1"),
  DARK_GREEN("&2"),
  DARK_AQUA("&3"),
  DARK_RED("&4"),
  DARK_PURPLE("&5"),
  GOLD("&6"),
  GRAY("&7"),
  DARK_GRAY("&8"),
  BLUE("&9"),
  GREEN("&a"),
  AQUA("&b"),
  RED("&c"),
  YELLOW("&e"),
  WHITE("&f");

  @Getter private final String color;
}
