package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.database.CacheManager;
import lee.code.towns.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PublicCMD extends SubCommand {
  private final Towns towns;

  public PublicCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "public";
  }

  @Override
  public String getDescription() {
    return "Toggle if your town is public or private.";
  }

  @Override
  public String getSyntax() {
    return "&e/towns public";
  }

  @Override
  public String getPermission() {
    return "towns.command.public";
  }

  @Override
  public boolean performAsync() {
    return true;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return true;
  }

  @Override
  public void perform(Player player, String[] args) {
    final CacheManager cacheManager = towns.getCacheManager();
    final UUID uuid = player.getUniqueId();
    if (!cacheManager.getCacheTowns().hasTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
      return;
    }
    final boolean result = !cacheManager.getCacheTowns().isTownPublic(uuid);
    cacheManager.getCacheTowns().setTownPublic(uuid, result);
    final String status = result ? Lang.PUBLIC.getString() : Lang.PRIVATE.getString();
    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_PUBLIC_SUCCESS.getComponent(new String[]{status})));
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
