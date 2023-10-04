package lee.code.towns.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LogBackendType;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lee.code.towns.Towns;
import lee.code.towns.database.tables.*;
import lee.code.towns.enums.PermissionType;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
  private final Towns towns;
  private final Object synchronizedThreadLock = new Object();
  private Dao<BankTable, UUID> bankDao;
  private Dao<ChunkTable, String> chunkDao;
  private Dao<TownsTable, UUID> townsDao;
  private Dao<PermissionTable, Integer> permissionDao;
  private Dao<RentTable, String> rentDao;
  private Dao<ServerTable, Integer> serverDao;
  private ConnectionSource connectionSource;

  public DatabaseManager(Towns towns) {
    this.towns = towns;
  }

  private String getDatabaseURL() {
    if (!towns.getDataFolder().exists()) towns.getDataFolder().mkdir();
    return "jdbc:sqlite:" + new File(towns.getDataFolder(), "database.db");
  }

  public void initialize(boolean debug) {
    if (!debug) LoggerFactory.setLogBackendFactory(LogBackendType.NULL);
    try {
      final String databaseURL = getDatabaseURL();
      connectionSource = new JdbcConnectionSource(
        databaseURL,
        "test",
        "test",
        DatabaseTypeUtils.createDatabaseType(databaseURL));
      createOrCacheTables();
      createDefaultServerData();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void closeConnection() {
    try {
      connectionSource.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void createOrCacheTables() throws SQLException {
    final CacheManager cacheManager = towns.getCacheManager();
    //Permission data
    TableUtils.createTableIfNotExists(connectionSource, PermissionTable.class);
    permissionDao = DaoManager.createDao(connectionSource, PermissionTable.class);

    //Bank data
    TableUtils.createTableIfNotExists(connectionSource, BankTable.class);
    bankDao = DaoManager.createDao(connectionSource, BankTable.class);

    //Chunk data
    TableUtils.createTableIfNotExists(connectionSource, ChunkTable.class);
    chunkDao = DaoManager.createDao(connectionSource, ChunkTable.class);

    for (ChunkTable chunkTable : chunkDao.queryForAll()) {
      cacheManager.getCacheChunks().setChunkTable(chunkTable);
      cacheManager.getCacheChunks().getChunkPermData().setPermissionTable(queryPermChunkTable(chunkTable));
    }

    //Renters data
    TableUtils.createTableIfNotExists(connectionSource, RentTable.class);
    rentDao = DaoManager.createDao(connectionSource, RentTable.class);

    for (RentTable rentTable : rentDao.queryForAll()) {
      cacheManager.getCacheRenters().setRentTable(rentTable);
    }

    //Server data
    TableUtils.createTableIfNotExists(connectionSource, ServerTable.class);
    serverDao = DaoManager.createDao(connectionSource, ServerTable.class);
    for (ServerTable serverTable : serverDao.queryForAll()) {
      cacheManager.getCacheServer().setServerTable(serverTable);
    }

    //Player data
    TableUtils.createTableIfNotExists(connectionSource, TownsTable.class);
    townsDao = DaoManager.createDao(connectionSource, TownsTable.class);

    for (TownsTable townsTable : townsDao.queryForAll()) {
      cacheManager.getCacheTowns().setTownsTable(townsTable);
      if (townsTable.getTown() != null) {
        cacheManager.getCacheTowns().getPermData().setPermissionTable(queryPermTownsTable(townsTable));
        cacheManager.getCacheTowns().getRoleData().setRolePermissionTable(queryPermTownsRoleTable(townsTable));
        cacheManager.getCacheBank().setBankTable(queryBankTownsTable(townsTable));
      }
    }
  }

  private void createDefaultServerData() {
    if (towns.getCacheManager().getCacheServer().getServerTable() == null) {
      final ServerTable serverTable = new ServerTable();
      towns.getCacheManager().getCacheServer().setServerTable(serverTable);
      createServerTable(serverTable);
    }
    towns.getCacheManager().startRentCollectionTask();
  }

  private PermissionTable queryPermChunkTable(ChunkTable chunkTable) {
    try {
      final QueryBuilder<PermissionTable, Integer> queryBuilder = permissionDao.queryBuilder();
      queryBuilder.where().eq("uuid", chunkTable.getOwner())
        .and()
        .like("permission_type", PermissionType.CHUNK)
        .and()
        .like("chunk", chunkTable.getChunk());
      return queryBuilder.query().get(0);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private List<BankTable> queryBankTownsTable(TownsTable townsTable) {
    try {
      final QueryBuilder<BankTable, UUID> queryBuilder = bankDao.queryBuilder();
      queryBuilder.where().eq("uniqueId", townsTable.getUniqueId());
      return queryBuilder.query();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private List<PermissionTable> queryPermTownsTable(TownsTable townsTable) {
    try {
      final QueryBuilder<PermissionTable, Integer> queryBuilder = permissionDao.queryBuilder();
      queryBuilder.where().eq("uuid", townsTable.getUniqueId())
        .and()
        .like("permission_type", PermissionType.TOWN);
      return queryBuilder.query();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private List<PermissionTable> queryPermTownsRoleTable(TownsTable townsTable) {
    try {
      final QueryBuilder<PermissionTable, Integer> queryBuilder = permissionDao.queryBuilder();
      queryBuilder.where().eq("uuid", townsTable.getUniqueId())
        .and()
        .like("permission_type", PermissionType.ROLE);
      return queryBuilder.query();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteAllChunkTables(UUID uuid) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
        try {
          chunkDao.executeRaw("DELETE FROM chunks WHERE owner = '" + uuid + "';");
          permissionDao.executeRaw("DELETE FROM permissions WHERE uuid = '" + uuid + "' AND permission_type = '" + PermissionType.CHUNK.name() + "';");
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void deleteAllRentTables(UUID uuid) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
        try {
          rentDao.executeRaw("DELETE FROM renters WHERE owner = '" + uuid + "';");
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void deleteAllRolePermissionTables(UUID uuid) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
        try {
          permissionDao.executeRaw("DELETE FROM permissions WHERE uuid = '" + uuid + "' AND permission_type = '" + PermissionType.ROLE.name() + "';");
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void createTownsTable(TownsTable townsTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          townsDao.createIfNotExists(townsTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void updateTownsTable(TownsTable townsTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          townsDao.update(townsTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void createChunkTable(ChunkTable chunkTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          chunkDao.createIfNotExists(chunkTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void createChunkAndPermissionTable(ChunkTable chunkTable, PermissionTable permissionTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          permissionDao.createIfNotExists(permissionTable);
          chunkDao.createIfNotExists(chunkTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void updateChunkTable(ChunkTable chunkTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          chunkDao.update(chunkTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void deleteChunkTable(ChunkTable chunkTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          chunkDao.delete(chunkTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void updatePermissionTable(PermissionTable permissionTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          permissionDao.update(permissionTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void createPermissionTable(PermissionTable permissionTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          permissionDao.createIfNotExists(permissionTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void createTownAndPermissionTable(TownsTable townsTable, PermissionTable permissionTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          permissionDao.createIfNotExists(permissionTable);
          townsDao.createIfNotExists(townsTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void deletePermissionTable(PermissionTable permissionTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          permissionDao.delete(permissionTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void createBankTable(BankTable bankTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          bankDao.createIfNotExists(bankTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void updateBankTable(BankTable bankTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          bankDao.update(bankTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void deleteBankTable(BankTable bankTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          bankDao.delete(bankTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void updateRentTable(RentTable rentTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          rentDao.update(rentTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void createRentTable(RentTable rentTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          rentDao.createIfNotExists(rentTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void deleteRentTable(RentTable rentTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          rentDao.delete(rentTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  private void createServerTable(ServerTable serverTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          serverDao.createIfNotExists(serverTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void updateServerTable(ServerTable serverTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
        try {
          serverDao.update(serverTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }
}
