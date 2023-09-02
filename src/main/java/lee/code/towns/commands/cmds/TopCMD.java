package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.cache.towns.data.TownCitizenData;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TopCMD extends SubCommand {
  private final Towns towns;

  public TopCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "top";
  }

  @Override
  public String getDescription() {
    return "Check town leaderboards.";
  }

  @Override
  public String getSyntax() {
    return "/t top &f<bank/size> <page>";
  }

  @Override
  public String getPermission() {
    return "towns.command.top";
  }

  @Override
  public boolean performAsync() {
    return true;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return false;
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length < 2) {
      player.sendMessage(Lang.PREFIX.getComponent(new String[]{getSyntax()}));
      return;
    }
    final String option = args[1].toLowerCase();
    switch (option) {
      case "bank" -> {
        final Map<UUID, Double> sortedBalances = CoreUtil.sortByValue(towns.getCacheManager().getCacheBank().getData().getTownBalances(), Comparator.reverseOrder());
        final ArrayList<UUID> players = new ArrayList<>(sortedBalances.keySet());
        int index;
        int page = 0;
        final int maxDisplayed = 10;
        if (args.length > 2) {
          if (CoreUtil.isPositiveIntNumber(args[2])) page = Integer.parseInt(args[2]);
        }
        int position = page * maxDisplayed + 1;
        final ArrayList<Component> lines = new ArrayList<>();
        lines.add(Lang.COMMAND_TOP_BANK_TITLE.getComponent(null));
        lines.add(Component.text(" "));

        for (int i = 0; i < maxDisplayed; i++) {
          index = maxDisplayed * page + i;
          if (index >= players.size()) break;
          final UUID targetID = players.get(index);
          lines.add(Lang.COMMAND_TOP_BANK_LINE.getComponent(new String[]{
            String.valueOf(position),
            towns.getCacheManager().getCacheTowns().getTownName(targetID),
            Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(sortedBalances.get(targetID))})
          }));
          position++;
        }

        if (lines.size() == 2) return;
        lines.add(Component.text(" "));
        lines.add(CoreUtil.createPageSelectionComponent("/towns top bank", page));
        for (Component line : lines) player.sendMessage(line);
      }
      case "size" -> {
        final TownCitizenData townCitizenData = towns.getCacheManager().getCacheTowns().getCitizenData();
        final Map<UUID, Integer> ownerCitizens = new HashMap<>();
        for (UUID owner : townCitizenData.getCitizenOwnerList()) ownerCitizens.put(owner, townCitizenData.getCitizenAmount(owner));
        final Map<UUID, Integer> sortedOwnerCitizens = CoreUtil.sortByValue(ownerCitizens, Comparator.reverseOrder());
        final ArrayList<UUID> owners = new ArrayList<>(sortedOwnerCitizens.keySet());

        int index;
        int page = 0;
        final int maxDisplayed = 10;
        if (args.length > 2) {
          if (CoreUtil.isPositiveIntNumber(args[2])) page = Integer.parseInt(args[2]);
        }
        int position = page * maxDisplayed + 1;
        final ArrayList<Component> lines = new ArrayList<>();
        lines.add(Lang.COMMAND_TOP_SIZE_TITLE.getComponent(null));
        lines.add(Component.text(" "));

        for (int i = 0; i < maxDisplayed; i++) {
          index = maxDisplayed * page + i;
          if (index >= owners.size()) break;
          final UUID targetID = owners.get(index);
          lines.add(Lang.COMMAND_TOP_SIZE_LINE.getComponent(new String[]{
            String.valueOf(position),
            towns.getCacheManager().getCacheTowns().getTownName(targetID),
            CoreUtil.parseValue(sortedOwnerCitizens.get(targetID))
          }));
          position++;
        }

        if (lines.size() == 2) return;
        lines.add(Component.text(" "));
        lines.add(CoreUtil.createPageSelectionComponent("/towns top size", page));
        for (Component line : lines) player.sendMessage(line);
      }
      default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
    }
  }

  @Override
  public void performConsole(CommandSender console, String[] args) {
    console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
  }

  @Override
  public void performSender(CommandSender sender, String[] args) {
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return new ArrayList<>();
  }
}
