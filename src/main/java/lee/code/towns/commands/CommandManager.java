package lee.code.towns.commands;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lee.code.towns.Towns;
import lee.code.towns.commands.cmds.*;
import lee.code.towns.lang.Lang;
import lee.code.towns.utils.CoreUtil;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager implements CommandExecutor {

    @Getter private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    private final ConcurrentHashMap<UUID, ScheduledTask> asyncTasks = new ConcurrentHashMap<>();
    private final Object synchronizedThreadLock = new Object();
    private final Towns towns;

    public CommandManager(Towns towns) {
        this.towns = towns;
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.add(new ChatCMD(towns));
        subCommands.add(new CreateCMD(towns));
        subCommands.add(new HelpCMD(towns));
        subCommands.add(new BorderCMD(towns));
        subCommands.add(new ClaimCMD(towns));
        subCommands.add(new AutoClaimCMD(towns));
        subCommands.add(new UnclaimCMD(towns));
        subCommands.add(new MapCMD(towns));
        subCommands.add(new SpawnCMD(towns));
        subCommands.add(new SetSpawnCMD(towns));
        subCommands.add(new FlagManagerCMD(towns));
        subCommands.add(new RoleCMD(towns));
        subCommands.add(new InviteCMD(towns));
        subCommands.add(new InfoCMD(towns));
        subCommands.add(new PublicCMD(towns));
        subCommands.add(new AbandonCMD(towns));
        subCommands.add(new LeaveCMD(towns));
        subCommands.add(new TeleportCMD());
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                for (SubCommand subCommand : subCommands) {
                    if (args[0].equalsIgnoreCase(subCommand.getName())) {
                        if (!player.hasPermission(subCommand.getPermission())) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PERMISSION.getComponent(null)));
                        if (subCommand.performAsync()) performAsync(player, subCommand, args);
                        else subCommand.perform(player, args);
                        return true;
                    }
                }
            }
            sendHelpMessage(player);
        } else if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    if (subCommand.performAsync()) performAsync(sender, subCommand, args);
                    else subCommand.performConsole(sender, args);
                    return true;
                }
            }
        }
        return true;
    }

    public void performAsync(CommandSender sender, SubCommand subCommand, String[] args) {
        if (sender instanceof Player player) {
            final UUID uuid = player.getUniqueId();
            if (asyncTasks.containsKey(uuid)) {
                if (!asyncTasks.get(uuid).getExecutionState().equals(ScheduledTask.ExecutionState.FINISHED)) {
                    player.sendMessage(Lang.ERROR_ONE_COMMAND_AT_A_TIME.getComponent(null));
                    return;
                }
            }
            if (subCommand.performAsyncSynchronized()) {
                synchronized (synchronizedThreadLock) {
                    performSubCommandAsync(player, uuid, subCommand, args);
                }
            } else {
                performSubCommandAsync(player, uuid, subCommand, args);
            }
        } else if (sender instanceof ConsoleCommandSender console) {
            Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> subCommand.performConsole(console, args));
        }
    }

    private void performSubCommandAsync(Player player, UUID uuid, SubCommand subCommand, String[] args) {
        asyncTasks.put(uuid, Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                subCommand.perform(player, args);
            } finally {
                asyncTasks.remove(uuid);
            }
        }));
    }

    public void sendHelpMessage(CommandSender sender) {
        int number = 1;
        final List<Component> lines = new ArrayList<>();
        lines.add(Lang.COMMAND_HELP_DIVIDER.getComponent(null));
        lines.add(Lang.COMMAND_HELP_TITLE.getComponent(null));
        lines.add(Component.text(""));

        //TODO fix <> issue
        for (SubCommand subCommand : subCommands) {
            if (sender.hasPermission(subCommand.getPermission())) {
                final Component helpSubCommand = Lang.COMMAND_HELP_SUB_COMMAND.getComponent(new String[] { String.valueOf(number), subCommand.getSyntax() })
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, CoreUtil.stripColorCodes(subCommand.getSyntax())))
                        .hoverEvent(Lang.COMMAND_HELP_SUB_COMMAND_HOVER.getComponent(new String[] { subCommand.getDescription() }));
                lines.add(helpSubCommand);
                number++;
            }
        }
        lines.add(Component.text(""));
        lines.add(Lang.COMMAND_HELP_DIVIDER.getComponent(null));
        for (Component line : lines) sender.sendMessage(line);
    }
}