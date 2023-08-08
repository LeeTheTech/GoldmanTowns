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

    @DatabaseField(columnName = "uuid", canBeNull = false)
    private UUID renter;

    @DatabaseField(columnName = "price", canBeNull = false)
    private double price;

    public RentTable(String chunk, UUID renter, double price) {
        this.chunk = chunk;
        this.renter = renter;
        this.price = price;
    }
}
