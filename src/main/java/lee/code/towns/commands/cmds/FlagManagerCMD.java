package lee.code.towns.commands.cmds;

import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.lang.Lang;
import lee.code.towns.menus.menu.FlagManager;
import lee.code.towns.menus.system.MenuPlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlagManagerCMD extends SubCommand {

    private final Towns towns;

    public FlagManagerCMD(Towns towns) {
        this.towns = towns;
    }

    @Override
    public String getName() {
        return "flagmanager";
    }

    @Override
    public String getDescription() {
        return "Open your town flag manager menu.";
    }

    @Override
    public String getSyntax() {
        return "/towns flagmanager";
    }

    @Override
    public String getPermission() {
        return "towns.command.flagmanager";
    }

    @Override
    public boolean performAsync() {
        return false;
    }

    @Override
    public boolean performAsyncSynchronized() {
        return false;
    }

    @Override
    public void perform(Player player, String[] args) {
        final UUID uuid = player.getUniqueId();
        if (!towns.getCacheManager().getCacheTowns().hasTown(uuid)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_TOWN_OWNER.getComponent(null)));
            return;
        }
        final MenuPlayerData menuPlayerData = towns.getMenuManager().getMenuPlayerData(uuid);
        towns.getMenuManager().openMenu(new FlagManager(menuPlayerData, towns), player);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
    }

    @Override
    public void performSender(CommandSender sender, String[] args) { }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
