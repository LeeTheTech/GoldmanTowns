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
@DatabaseTable(tableName = "towns")
public class TownsTable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID uniqueId;

    @DatabaseField(columnName = "town")
    private String town;

    @DatabaseField(columnName = "joined_town")
    private UUID joinedTown;

    @DatabaseField(columnName = "town_members")
    private String townMembers;

    @DatabaseField(columnName = "spawn")
    private String spawn;

    @DatabaseField(columnName = "last_joined")
    private String lastJoined;

    @DatabaseField(columnName = "roles")
    private String roles;

    @DatabaseField(columnName = "citizen_roles")
    private String citizenRoles;

    public TownsTable(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
