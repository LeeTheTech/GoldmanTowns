package lee.code.towns.database.cache.renters;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.tables.RentTable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheRenters extends DatabaseHandler {

    private final ConcurrentHashMap<String, RentTable> rentCache = new ConcurrentHashMap<>();

    public CacheRenters(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    private RentTable getRentTable(String chunk) {
        return rentCache.get(chunk);
    }

    public void setRentTable(RentTable rentTable) {
     rentCache.put(rentTable.getChunk(), rentTable);
    }

    public void createNewRenter(UUID uuid, String chunk, double price) {
        final RentTable rentTable = new RentTable(chunk, uuid, price);
        rentCache.put(rentTable.getChunk(), rentTable);
        createRentDatabase(rentTable);
    }

    public void deleteRentedChunk(String chunk) {
        deleteRentDatabase(getRentTable(chunk));
    }

    public boolean isRentedChunk(String chunk) {
        return rentCache.containsKey(chunk);
    }

    public boolean isPlayerRentingChunk(UUID uuid, String chunk) {
        return rentCache.get(chunk).getRenter().equals(uuid);
    }

}
