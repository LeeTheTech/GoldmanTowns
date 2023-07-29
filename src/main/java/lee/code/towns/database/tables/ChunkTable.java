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
@DatabaseTable(tableName = "chunks")
public class ChunkTable {

    @DatabaseField(id = true, canBeNull = false)
    private String chunk;

    @DatabaseField(columnName = "owner", canBeNull = false)
    private UUID owner;

    public ChunkTable(String chunk, UUID owner) {
        this.chunk = chunk;
        this.owner = owner;
    }
}
