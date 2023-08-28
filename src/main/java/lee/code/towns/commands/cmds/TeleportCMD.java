package lee.code.towns.commands.cmds;

import lee.code.towns.commands.SubCommand;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeleportCMD extends SubCommand {

  @Override
  public String getName() {
    return "teleport";
  }

  @Override
  public String getDescription() {
    return "Teleport to a target chunk you own or a town that is public.";
  }

  @Override
  public String getSyntax() {
    return "/towns teleport &f<chunk/town> <chunk/town>";
  }

  @Override
  public String getPermission() {
    return "towns.command.teleport";
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
    //TODO check if valid chunk to teleport to
    //TODO check if owner or renting chunk
    final String option = args[1].toLowerCase();
    final String target = args[2].toLowerCase();
    switch (option) {
      case "chunk" -> ChunkUtil.teleportToMiddleOfChunk(player, target);
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
