package lee.code.towns.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "players")
public class PlayerTable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID uniqueID;

    @DatabaseField(columnName = "town")
    private String town;

    @DatabaseField(columnName = "joined_town")
    private String joinedTown;

    @DatabaseField(columnName = "town_members")
    private String townMembers;

    @DatabaseField(columnName = "last_joined")
    private Date lastJoined;

    public PlayerTable(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }
}
