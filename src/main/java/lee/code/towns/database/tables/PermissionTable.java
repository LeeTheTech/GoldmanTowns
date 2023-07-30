package lee.code.towns.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lee.code.towns.enums.PermissionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "permissions")
public class PermissionTable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "uuid", canBeNull = false)
    private UUID uniqueID;

    @DatabaseField(columnName = "permission_type", canBeNull = false)
    private PermissionType permissionType;

    @DatabaseField(columnName = "chunk")
    private String chunk;

    public PermissionTable(UUID uniqueID, PermissionType permissionType) {
        this.uniqueID = uniqueID;
        this.permissionType = permissionType;
    }
}
