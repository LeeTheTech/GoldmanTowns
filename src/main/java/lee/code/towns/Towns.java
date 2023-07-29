package lee.code.towns;

import lee.code.towns.commands.CommandManager;
import lee.code.towns.commands.TabCompletion;
import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.CacheManager;
import lee.code.towns.listeners.JoinListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Towns extends JavaPlugin {

    @Getter private CacheManager cacheManager;
    @Getter private CommandManager commandManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        this.databaseManager = new DatabaseManager(this);
        this.cacheManager = new CacheManager(databaseManager);
        this.commandManager = new CommandManager();

        databaseManager.initialize(true);
        registerListeners();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }

    private void registerCommands() {
        getCommand("towns").setExecutor(commandManager);
        getCommand("towns").setTabCompleter(new TabCompletion(this));

    }
}
