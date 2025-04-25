package lee.code.towns.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "bank")
public class BankTable {

  @DatabaseField(id = true, canBeNull = false)
  private UUID uniqueId;

  @DatabaseField(columnName = "balance")
  private double balance;

  public BankTable(UUID uuid) {
    this.uniqueId = uuid;
  }
}
