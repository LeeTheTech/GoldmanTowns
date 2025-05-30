package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.BorderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.BorderParticleManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BorderCMD extends SubCommand {
  private final Towns towns;

  public BorderCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "border";
  }

  @Override
  public String getDescription() {
    return "Toggle town border to view particles around claimed chunks.";
  }

  @Override
  public String getSyntax() {
    return "/t border &f<town, rented, chunk, off>";
  }

  @Override
  public String getPermission() {
    return "towns.command.border";
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
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    String option = args[1].toLowerCase();
    CacheManager cacheManager = towns.getCacheManager();
    UUID playerID = player.getUniqueId();
    BorderParticleManager borderParticleManager = towns.getBorderParticleManager();
    switch (option) {
      case "town" -> {
        if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(playerID)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
          return;
        }
        if (borderParticleManager.hasBorderActive(playerID)) borderParticleManager.stopBorder(playerID);
        borderParticleManager.scheduleBorder(player, BorderType.valueOf(option.toUpperCase()));
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_SUCCESS.getComponent(new String[]{option, Lang.ON.getString()})));
      }
      case "chunk" -> {
        if (borderParticleManager.hasBorderActive(playerID)) borderParticleManager.stopBorder(playerID);
        borderParticleManager.scheduleBorder(player, BorderType.valueOf(option.toUpperCase()));
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_SUCCESS.getComponent(new String[]{option, Lang.ON.getString()})));
      }
      case "rented" -> {
        if (!cacheManager.getCacheRenters().getRenterListData().hasRentedChunks(playerID)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BORDER_NONE_RENTED.getComponent(null)));
          return;
        }
        if (borderParticleManager.hasBorderActive(playerID)) borderParticleManager.stopBorder(playerID);
        borderParticleManager.scheduleBorder(player, BorderType.valueOf(option.toUpperCase()));
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_SUCCESS.getComponent(new String[]{option, Lang.ON.getString()})));
      }
      case "off" -> {
        if (borderParticleManager.hasBorderActive(playerID)) borderParticleManager.stopBorder(playerID);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BORDER_OFF_SUCCESS.getComponent(new String[]{Lang.OFF.getString()})));
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
