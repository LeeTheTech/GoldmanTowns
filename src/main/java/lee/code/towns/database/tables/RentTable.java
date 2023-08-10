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
@DatabaseTable(tableName = "renters")
public class RentTable {

    @DatabaseField(id = true, canBeNull = false)
    private String chunk;

    @DatabaseField(columnName = "owner", canBeNull = false)
    private UUID owner;

    @DatabaseField(columnName = "renter")
    private UUID renter;

    @DatabaseField(columnName = "price", canBeNull = false)
    private double price;

    public RentTable(String chunk, UUID owner, double price) {
        this.chunk = chunk;
        this.owner = owner;
        this.price = price;
    }
}
