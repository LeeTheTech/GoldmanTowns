package lee.code.towns.commands;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lee.code.towns.Towns;
import lee.code.towns.commands.cmds.*;
import lee.code.towns.lang.Lang;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager implements CommandExecutor {
  @Getter private final ConcurrentHashMap<String, SubCommand> subCommands = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, ScheduledTask> asyncTasks = new ConcurrentHashMap<>();
  private final Object synchronizedThreadLock = new Object();
  private final Towns towns;

  public CommandManager(Towns towns) {
    this.towns = towns;
    storeSubCommands();
  }

  private void storeSubCommands() {
    storeSubCommand(new ChatCMD(towns));
    storeSubCommand(new ChatCMD(towns));
    storeSubCommand(new CreateCMD(towns));
    storeSubCommand(new HelpCMD(towns));
    storeSubCommand(new BorderCMD(towns));
    storeSubCommand(new ClaimCMD(towns));
    storeSubCommand(new AutoClaimCMD(towns));
    storeSubCommand(new AutoMapCMD(towns));
    storeSubCommand(new UnclaimCMD(towns));
    storeSubCommand(new MapCMD(towns));
    storeSubCommand(new SpawnCMD(towns));
    storeSubCommand(new SetSpawnCMD(towns));
    storeSubCommand(new FlagManagerCMD(towns));
    storeSubCommand(new RoleCMD(towns));
    storeSubCommand(new InviteCMD(towns));
    storeSubCommand(new InfoCMD(towns));
    storeSubCommand(new PublicCMD(towns));
    storeSubCommand(new AbandonCMD(towns));
    storeSubCommand(new LeaveCMD(towns));
    storeSubCommand(new ChunkInfoCMD(towns));
    storeSubCommand(new RentCMD(towns));
    storeSubCommand(new BonusClaimsCMD(towns));
    storeSubCommand(new BankCMD(towns));
    storeSubCommand(new KickCMD(towns));
    storeSubCommand(new SetBannerCMD(towns));
    storeSubCommand(new BannerCMD(towns));
    storeSubCommand(new TeleportCMD(towns));
  }

  private void storeSubCommand(SubCommand subCommand) {
    subCommands.put(subCommand.getName(), subCommand);
  }

  public SubCommand getSubCommand(String command) {
    return subCommands.get(command);
  }

  public List<SubCommand> getSubCommandList() {
    return new ArrayList<>(subCommands.values());
  }

  @Override
  public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
    if (args.length > 0) {
      if (sender instanceof Player player) {
        if (subCommands.containsKey(args[0].toLowerCase())) {
          final SubCommand subCommand = getSubCommand(args[0].toLowerCase());
          if (!player.hasPermission(subCommand.getPermission())) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PERMISSION.getComponent(null)));
            return true;
          }
          if (subCommand.performAsync()) performAsync(player, subCommand, args);
          else subCommand.perform(player, args);
          return true;
        }
      } else {
        if (subCommands.containsKey(args[0].toLowerCase())) {
          final SubCommand subCommand = getSubCommand(args[0].toLowerCase());
          if (subCommand.performAsync()) performAsync(sender, subCommand, args);
          else subCommand.performConsole(sender, args);
          return true;
        }
      }
    }
    getSubCommand("help").performSender(sender, args);
    return true;
  }

  public void performAsync(CommandSender sender, SubCommand subCommand, String[] args) {
    if (sender instanceof Player player) {
      final UUID uuid = player.getUniqueId();
      if (asyncTasks.containsKey(uuid)) {
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ONE_COMMAND_AT_A_TIME.getComponent(null)));
        return;
      }
      if (subCommand.performAsyncSynchronized()) {
        synchronized (synchronizedThreadLock) {
          performPlayerSubCommandAsync(player, uuid, subCommand, args);
        }
      } else {
        performPlayerSubCommandAsync(player, uuid, subCommand, args);
      }
    } else if (sender instanceof ConsoleCommandSender console) {
      if (subCommand.performAsync()) {
        if (subCommand.performAsyncSynchronized()) {
          synchronized (synchronizedThreadLock) {
            performConsoleSubCommandAsync(sender, args, subCommand);
          }
        } else {
          performConsoleSubCommandAsync(sender, args, subCommand);
        }
      } else {
        subCommand.performConsole(console, args);
      }
    }
  }

  private void performPlayerSubCommandAsync(Player player, UUID uuid, SubCommand subCommand, String[] args) {
    asyncTasks.put(uuid, Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
      try {
        subCommand.perform(player, args);
      } finally {
        asyncTasks.remove(uuid);
      }
    }));
  }

  private void performConsoleSubCommandAsync(CommandSender sender, String[] args, SubCommand subCommand) {
    Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> subCommand.performConsole(sender, args));
  }
}
