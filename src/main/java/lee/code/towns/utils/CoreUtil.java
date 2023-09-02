package lee.code.towns.utils;

import lee.code.towns.lang.Lang;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CoreUtil {
  @Getter private final static Object synchronizedThreadLock = new Object();
  private final static DecimalFormat amountFormatter = new DecimalFormat("#,###.##");
  private final static Pattern numberDoublePattern = Pattern.compile("^(?=.*[1-9])(\\d*\\.?\\d*)$");
  private final static Pattern numberIntPattern = Pattern.compile("^[1-9]\\d*$");

  public static String parseValue(int value) {
    if (value == 0) return "0";
    return amountFormatter.format(value);
  }

  public static String parseValue(double value) {
    if (value == 0) return "0";
    return amountFormatter.format(value);
  }

  public static Component parseColorComponent(String text) {
    final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
    return (Component.empty().decoration(TextDecoration.ITALIC, false)).append(serializer.deserialize(text));
  }

  public static String stripColorCodes(String text) {
    return PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
  }

  public static String getTextBeforeCharacter(String input, char character) {
    final int index = input.indexOf(character);
    if (index == -1) return input;
    else return input.substring(0, index);
  }

  public static List<String> getOnlinePlayers() {
    return Bukkit.getOnlinePlayers().stream()
      .filter(player -> !player.getGameMode().equals(GameMode.SPECTATOR))
      .map(Player::getName)
      .collect(Collectors.toList());
  }

  public static String buildStringFromArgs(String[] words, int startIndex) {
    final StringBuilder sb = new StringBuilder();
    for (int i = startIndex; i < words.length; i++) {
      sb.append(words[i]);
      if (i < words.length - 1) sb.append(" ");
    }
    return sb.toString();
  }

  public static String serializeLocation(Location location) {
    if (location == null) return null;
    else if (location.getWorld() == null) return null;
    return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
  }

  public static Location parseLocation(String location) {
    if (location == null) return null;
    final String[] split = location.split(",", 6);
    return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), (float) Double.parseDouble(split[4]), (float) Double.parseDouble(split[5]));
  }

  public static BlockFace getPlayerFacingDirection(Player player) {
    final Vector direction = player.getLocation().getDirection();
    final double dx = direction.getX();
    final double dz = direction.getZ();

    if (Math.abs(dx) > Math.abs(dz)) {
      if (dx < 0) {
        return BlockFace.WEST;
      } else {
        return BlockFace.EAST;
      }
    } else {
      if (dz < 0) {
        return BlockFace.NORTH;
      } else {
        return BlockFace.SOUTH;
      }
    }
  }

  public static String capitalize(String message) {
    final String format = message.toLowerCase().replaceAll("_", " ");
    return StringUtils.capitalize(format);
  }

  public static String removeSpecialCharacters(String input) {
    final StringBuilder output = new StringBuilder();
    final String regex = "[^a-zA-Z0-9\\s]";
    for (int i = 0; i < input.length(); i++) {
      final char c = input.charAt(i);
      if (Character.toString(c).matches(regex)) continue;
      output.append(c);
    }
    return output.toString();
  }

  public static void sendConfirmMessage(Player player, Component message, String command, Component hoverYes, Component hoverNo, boolean isConfirm) {
    final ArrayList<Component> lines = new ArrayList<>();
    final Component yes;
    if (isConfirm) {
      yes = Lang.CONFIRM.getComponent(null)
        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command + " confirm"))
        .hoverEvent(hoverYes);
    } else {
      yes = Lang.ACCEPT.getComponent(null)
        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command + " accept"))
        .hoverEvent(hoverYes);
    }
    final Component no = Lang.DENY.getComponent(null)
      .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command + " deny"))
      .hoverEvent(hoverNo);
    final Component click = Lang.CLICK.getComponent(null);
    lines.add(message);
    lines.add(click.append(yes.append(Component.text("  ")).append(no)));
    for (Component line : lines) player.sendMessage(line);
  }

  public static String parseTime(long time) {
    final long days = TimeUnit.MILLISECONDS.toDays(time);
    final long hours = TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS.toHours(days);
    final long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days);
    final long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days);
    if (days != 0L) return "&e" + days + "&6d&e, " + hours + "&6h&e, " + minutes + "&6m&e, " + seconds + "&6s";
    else if (hours != 0L) return "&e" + hours + "&6h&e, " + minutes + "&6m&e, " + seconds + "&6s";
    else return minutes != 0L ? "&e" + minutes + "&6m&e, " + seconds + "&6s" : "&e" + seconds + "&6s";
  }

  public static boolean isPositiveDoubleNumber(String numbers) {
    return numberDoublePattern.matcher(numbers).matches();
  }

  public static boolean isPositiveIntNumber(String numbers) {
    return numberIntPattern.matcher(numbers).matches();
  }

  public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(Map<K, V> hm, Comparator<V> comparator) {
    final HashMap<K, V> temp = new LinkedHashMap<>();
    hm.entrySet().stream()
      .sorted(Map.Entry.comparingByValue(comparator))
      .forEachOrdered(entry -> temp.put(entry.getKey(), entry.getValue()));
    return temp;
  }

  public static Component createPageSelectionComponent(String command, int page) {
    final Component next = Lang.NEXT_PAGE_TEXT.getComponent(null).hoverEvent(Lang.NEXT_PAGE_HOVER.getComponent(null)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (page + 1)));
    final Component split = Lang.PAGE_SPACER_TEXT.getComponent(null);
    final Component prev = Lang.PREVIOUS_PAGE_TEXT.getComponent(null).hoverEvent(Lang.PREVIOUS_PAGE_HOVER.getComponent(null)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (page - 1)));
    return prev.append(split).append(next);
  }

  public static long millisecondsToMidnightPST() {
    final Calendar losAngelesCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
    losAngelesCalendar.set(Calendar.HOUR_OF_DAY, 0);
    losAngelesCalendar.set(Calendar.MINUTE, 0);
    losAngelesCalendar.set(Calendar.SECOND, 0);
    losAngelesCalendar.set(Calendar.MILLISECOND, 0);

    final long currentTime = System.currentTimeMillis();
    final long midnightPST = losAngelesCalendar.getTimeInMillis();
    long millisecondsRemaining = midnightPST - currentTime;
    if (millisecondsRemaining < 0) millisecondsRemaining += 24 * 60 * 60 * 1000;
    return millisecondsRemaining;
  }
}
