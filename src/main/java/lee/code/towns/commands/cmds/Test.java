package lee.code.towns.commands.cmds;

import lee.code.towns.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Test extends SubCommand {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "testing";
    }

    @Override
    public String getSyntax() {
        return "test";
    }

    @Override
    public String getPermission() {
        return "towns.command.test";
    }

    @Override
    public void perform(Player player, String[] args) {

    }

    @Override
    public void performConsole(CommandSender console, String[] args) {

    }

    @Override
    public void performSender(CommandSender sender, String[] args) {
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
