package lee.code.towns.database.cache.bank.data;

import com.google.common.util.concurrent.AtomicDouble;
import lee.code.towns.database.cache.bank.CacheBank;
import lee.code.towns.database.tables.BankTable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BalanceData {
  private final CacheBank cacheBank;
  private final ConcurrentHashMap<UUID, AtomicDouble> balanceCache = new ConcurrentHashMap<>();

  public BalanceData(CacheBank cacheBank) {
    this.cacheBank = cacheBank;
  }

  public void setBalanceCache(BankTable bankTable) {
    balanceCache.put(bankTable.getUniqueId(), new AtomicDouble(bankTable.getBalance()));
  }

  private AtomicDouble getAtomicBalance(UUID uuid) {
    return balanceCache.get(uuid);
  }

  public double getTownBalance(UUID uuid) {
    return balanceCache.get(uuid).get();
  }

  public void addTownBalance(UUID uuid, double amount) {
    final BankTable bankTable = cacheBank.getBankTable(uuid);
    getAtomicBalance(uuid).addAndGet(amount);
    bankTable.setBalance(getAtomicBalance(uuid).get());
    cacheBank.updateBankDatabase(bankTable);
  }

  public void removeTownBalance(UUID uuid, double amount) {
    final BankTable bankTable = cacheBank.getBankTable(uuid);
    getAtomicBalance(uuid).updateAndGet(currentValue -> Math.max(currentValue - amount, 0));
    bankTable.setBalance(getAtomicBalance(uuid).get());
    cacheBank.updateBankDatabase(bankTable);
  }

  public Map<UUID, Double> getTownBalances() {
    return balanceCache.entrySet()
      .stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> entry.getValue().get()
      ));
  }

  public void removeTownBalanceData(UUID uuid) {
    balanceCache.remove(uuid);
  }
}
