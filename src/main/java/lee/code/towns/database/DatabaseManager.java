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
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final Towns towns;
    private Dao<ChunkTable, String> chunkDao;
    private Dao<TownsTable, UUID> townsDao;
    private Dao<PermissionTable, Integer> permissionDao;
    private Dao<RentTable, Integer> rentDao;
    private Dao<ServerTable, Integer> serverDao;

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    public DatabaseManager(Towns towns) {
        this.towns = towns;
    }

    private String getDatabaseURL() {
        //Setup MongoDB
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
            cacheManager.getCacheTowns().getPermData().setPermissionTable(queryPermTownsTable(townsTable));
            cacheManager.getCacheTowns().getRoleData().setRolePermissionTable(queryPermTownsRoleTable(townsTable));
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

    private PermissionTable queryPermTownsTable(TownsTable townsTable) {
        try {
            final QueryBuilder<PermissionTable, Integer> queryBuilder = permissionDao.queryBuilder();
            queryBuilder.where().eq("uuid", townsTable.getUniqueId())
                    .and()
                    .like("permission_type", PermissionType.TOWN);
            return queryBuilder.query().get(0);
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

    public synchronized void deleteAllChunkTables(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
            try {
                chunkDao.executeRaw("DELETE FROM chunks WHERE owner = '" + uuid + "';");
                permissionDao.executeRaw("DELETE FROM permissions WHERE uuid = '" + uuid + "' AND permission_type = '" + PermissionType.CHUNK.name() + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteAllRentTables(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
            try {
                rentDao.executeRaw("DELETE FROM renters WHERE owner = '" + uuid + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteAllRolePermissionTables(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(towns, () -> {
            try {
                permissionDao.executeRaw("DELETE FROM permissions WHERE uuid = '" + uuid + "' AND permission_type = '" + PermissionType.ROLE.name() + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createTownsTable(TownsTable townsTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                townsDao.createIfNotExists(townsTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateTownsTable(TownsTable townsTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                townsDao.update(townsTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createChunkTable(ChunkTable chunkTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                chunkDao.createIfNotExists(chunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createChunkAndPermissionTable(ChunkTable chunkTable, PermissionTable permissionTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                permissionDao.createIfNotExists(permissionTable);
                chunkDao.createIfNotExists(chunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateChunkTable(ChunkTable chunkTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                chunkDao.update(chunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteChunkTable(ChunkTable chunkTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                chunkDao.delete(chunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updatePermissionTable(PermissionTable permissionTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                permissionDao.update(permissionTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createPermissionTable(PermissionTable permissionTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                permissionDao.createIfNotExists(permissionTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createTownAndPermissionTable(TownsTable townsTable, PermissionTable permissionTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                permissionDao.createIfNotExists(permissionTable);
                townsDao.createIfNotExists(townsTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deletePermissionTable(PermissionTable permissionTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                permissionDao.delete(permissionTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateRentTable(RentTable rentTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                rentDao.update(rentTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createRentTable(RentTable rentTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                rentDao.createIfNotExists(rentTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteRentTable(RentTable rentTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                rentDao.delete(rentTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private synchronized void createServerTable(ServerTable serverTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                serverDao.createIfNotExists(serverTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateServerTable(ServerTable serverTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                serverDao.update(serverTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
