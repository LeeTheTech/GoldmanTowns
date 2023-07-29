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
import lee.code.towns.database.cache.CacheManager;
import lee.code.towns.database.tables.ChunkTable;
import lee.code.towns.database.tables.PermissionTable;
import lee.code.towns.database.tables.PermissionType;
import lee.code.towns.database.tables.PlayerTable;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {

    private final Towns towns;
    private final CacheManager cacheManager;
    private Dao<ChunkTable, String> chunkDao;
    private Dao<PlayerTable, UUID> playerDao;
    private Dao<PermissionTable, Integer> permissionDao;

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    public DatabaseManager(Towns towns) {
        this.towns = towns;
        this.cacheManager = towns.getCacheManager();
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
        //Permission data
        TableUtils.createTableIfNotExists(connectionSource, PermissionTable.class);
        permissionDao = DaoManager.createDao(connectionSource, PermissionTable.class);

        //Chunk data
        TableUtils.createTableIfNotExists(connectionSource, ChunkTable.class);
        chunkDao = DaoManager.createDao(connectionSource, ChunkTable.class);

        for (ChunkTable chunkTable : chunkDao.queryForAll()) {
            cacheManager.getCacheChunks().setChunkTable(chunkTable);
            cacheManager.getCacheChunks().setPermissionTable(queryPermChunkTable(chunkTable));
        }

        //Player data
        TableUtils.createTableIfNotExists(connectionSource, PlayerTable.class);
        playerDao = DaoManager.createDao(connectionSource, PlayerTable.class);

        for (PlayerTable playerTable : playerDao.queryForAll()) {
            cacheManager.getCachePlayers().setPlayerTable(playerTable);
            cacheManager.getCachePlayers().setPermissionTable(queryPermPlayerTable(playerTable));
        }
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

    private PermissionTable queryPermPlayerTable(PlayerTable playerTable) {
        try {
            final QueryBuilder<PermissionTable, Integer> queryBuilder = permissionDao.queryBuilder();
            queryBuilder.where().eq("uuid", playerTable)
                    .and()
                    .like("permission_type", PermissionType.PLAYER);
            return queryBuilder.query().get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void createPlayerTable(PlayerTable playerTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                playerDao.create(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updatePlayerTable(PlayerTable playerTable) {
        Bukkit.getAsyncScheduler().runNow(towns, scheduledTask -> {
            try {
                playerDao.update(playerTable);
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
                permissionDao.create(permissionTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
