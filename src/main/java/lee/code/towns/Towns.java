package lee.code.towns;

import lee.code.towns.commands.CommandManager;
import lee.code.towns.commands.TabCompletion;
import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.CacheManager;
import lee.code.towns.listeners.AutoClaimListener;
import lee.code.towns.listeners.JoinListener;
import lee.code.towns.listeners.QuitListener;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.managers.BorderParticleManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Towns extends JavaPlugin {

    @Getter private CacheManager cacheManager;
    @Getter private CommandManager commandManager;
    @Getter private BorderParticleManager borderParticleManager;
    @Getter private AutoClaimManager autoClaimManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        this.borderParticleManager = new BorderParticleManager(this);
        this.autoClaimManager = new AutoClaimManager();
        this.databaseManager = new DatabaseManager(this);
        this.cacheManager = new CacheManager(databaseManager);
        this.commandManager = new CommandManager(this);
        registerCommands();
        registerListeners();

        databaseManager.initialize(true);
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AutoClaimListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);
    }

    private void registerCommands() {
        getCommand("towns").setExecutor(commandManager);
        getCommand("towns").setTabCompleter(new TabCompletion(this));

    }
}
