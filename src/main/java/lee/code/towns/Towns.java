package lee.code.towns;

import lee.code.towns.commands.ChatCommand;
import lee.code.towns.commands.CommandManager;
import lee.code.towns.commands.TabCompletion;
import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.CacheManager;
import lee.code.towns.listeners.AutoClaimListener;
import lee.code.towns.listeners.ChatListener;
import lee.code.towns.listeners.JoinListener;
import lee.code.towns.listeners.QuitListener;
import lee.code.towns.listeners.chunks.*;
import lee.code.towns.managers.AutoClaimManager;
import lee.code.towns.managers.BorderParticleManager;
import lee.code.towns.managers.ChatChannelManager;
import lee.code.towns.managers.InviteManager;
import lee.code.towns.menus.system.MenuListener;
import lee.code.towns.menus.system.MenuManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Towns extends JavaPlugin {

    @Getter private CacheManager cacheManager;
    @Getter private CommandManager commandManager;
    @Getter private ChatChannelManager chatChannelManager;
    @Getter private BorderParticleManager borderParticleManager;
    @Getter private AutoClaimManager autoClaimManager;
    @Getter private MenuManager menuManager;
    @Getter private InviteManager inviteManager;
    @Getter private Data data;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        this.borderParticleManager = new BorderParticleManager(this);
        this.autoClaimManager = new AutoClaimManager();
        this.databaseManager = new DatabaseManager(this);
        this.cacheManager = new CacheManager(this, databaseManager);
        this.commandManager = new CommandManager(this);
        this.chatChannelManager = new ChatChannelManager(this);
        this.menuManager = new MenuManager();
        this.inviteManager = new InviteManager(this);
        this.data = new Data();

        registerCommands();
        registerListeners();

        databaseManager.initialize(true);
        data.loadData();
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AutoClaimListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(menuManager), this);
        getServer().getPluginManager().registerEvents(new BreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BuildListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getServer().getPluginManager().registerEvents(new MonsterSpawningListener(this), this);
        getServer().getPluginManager().registerEvents(new PvEListener(this), this);
        getServer().getPluginManager().registerEvents(new PvPListener(this), this);
        getServer().getPluginManager().registerEvents(new RedstoneListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    private void registerCommands() {
        getCommand("towns").setExecutor(commandManager);
        getCommand("towns").setTabCompleter(new TabCompletion(this));
        getCommand("tc").setExecutor(new ChatCommand(this));
        getCommand("tc").setTabCompleter(new ChatCommand(this));

    }
}
