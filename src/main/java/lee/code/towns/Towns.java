package lee.code.towns;

import com.mojang.brigadier.tree.LiteralCommandNode;
import lee.code.towns.commands.*;
import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.CacheManager;
import lee.code.towns.listeners.*;
import lee.code.towns.listeners.chunks.*;
import lee.code.towns.managers.*;
import lee.code.towns.menus.system.MenuListener;
import lee.code.towns.menus.system.MenuManager;
import lombok.Getter;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Towns extends JavaPlugin {
  @Getter private static Towns instance;
  @Getter private CacheManager cacheManager;
  @Getter private CommandManager commandManager;
  @Getter private ChatChannelManager chatChannelManager;
  @Getter private BorderParticleManager borderParticleManager;
  @Getter private AutoMessageManager autoMessageManager;
  @Getter private AutoClaimManager autoClaimManager;
  @Getter private MapManager mapManager;
  @Getter private FlyManager flyManager;
  @Getter private MenuManager menuManager;
  @Getter private InviteManager inviteManager;
  @Getter private Data data;
  private DatabaseManager databaseManager;

  @Override
  public void onEnable() {
    instance = this;
    this.borderParticleManager = new BorderParticleManager(this);
    this.autoClaimManager = new AutoClaimManager();
    this.mapManager = new MapManager(this);
    this.autoMessageManager = new AutoMessageManager();
    this.databaseManager = new DatabaseManager(this);
    this.cacheManager = new CacheManager(this, databaseManager);
    this.commandManager = new CommandManager(this);
    this.chatChannelManager = new ChatChannelManager(this);
    this.flyManager = new FlyManager(this);
    this.menuManager = new MenuManager();
    this.inviteManager = new InviteManager(this);
    this.data = new Data();

    registerCommands();
    registerListeners();

    databaseManager.initialize(false);
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
    getServer().getPluginManager().registerEvents(new AutoMessageListener(this), this);
    getServer().getPluginManager().registerEvents(new AutoMapListener(this), this);
    getServer().getPluginManager().registerEvents(new FireSpreadListener(this), this);
    getServer().getPluginManager().registerEvents(new IceMeltListener(this), this);
    getServer().getPluginManager().registerEvents(new FlyListener(this), this);
  }

  private void registerCommands() {
    getCommand("towns").setExecutor(commandManager);
    getCommand("towns").setTabCompleter(new TabCompletion(commandManager));
    getCommand("tc").setExecutor(new ChatCommand(this));
    getCommand("tc").setTabCompleter(new ChatCommand(this));
    loadCommodoreData();
  }

  private void loadCommodoreData() {
    try {
      LiteralCommandNode<?> towns = CommodoreFileReader.INSTANCE.parse(getResource("towns.commodore"));
      CommodoreProvider.getCommodore(this).register(getCommand("towns"), towns);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
