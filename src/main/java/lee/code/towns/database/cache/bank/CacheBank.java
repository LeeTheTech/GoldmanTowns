package lee.code.towns.database.cache.bank;

import lee.code.towns.database.DatabaseManager;
import lee.code.towns.database.cache.bank.data.BalanceData;
import lee.code.towns.database.cache.handlers.DatabaseHandler;
import lee.code.towns.database.tables.BankTable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheBank extends DatabaseHandler {
  @Getter private final BalanceData data;
  private final ConcurrentHashMap<UUID, BankTable> bankCache = new ConcurrentHashMap<>();

  public CacheBank(DatabaseManager databaseManager) {
    super(databaseManager);
    this.data = new BalanceData(this);
  }

  public void setBankTable(BankTable bankTable) {
    bankCache.put(bankTable.getUniqueId(), bankTable);
    data.setBalanceCache(bankTable);
  }

  public void setBankTable(List<BankTable> bankTables) {
    for (BankTable bankTable : bankTables) {
      bankCache.put(bankTable.getUniqueId(), bankTable);
      data.setBalanceCache(bankTable);
    }
  }

  public BankTable getBankTable(UUID uuid) {
    return bankCache.get(uuid);
  }

  public void deleteAllBankData(UUID uuid) {
    data.removeTownBalanceData(uuid);
    deleteBankDatabase(getBankTable(uuid));
  }

  public void createBankData(UUID uuid) {
    final BankTable bankTable = new BankTable(uuid);
    data.setBalanceCache(bankTable);
    createBankDatabase(bankTable);
  }
}
