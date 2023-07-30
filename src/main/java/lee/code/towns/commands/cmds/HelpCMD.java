package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HelpCMD extends SubCommand {
    private final Towns towns;

    public HelpCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "List of commands you can use with this plugin.";
    }

    @Override
    public String getSyntax() {
        return "/towns help";
    }

    @Override
    public String getPermission() {
        return "towns.command.help";
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
        performSender(player, args);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        performSender(console, args);
    }

    @Override
    public void performSender(CommandSender sender, String[] args) {
        towns.getCommandManager().sendHelpMessage(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
