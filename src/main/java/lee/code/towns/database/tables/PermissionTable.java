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

    @DatabaseField(columnName = "role")
    private String role;

    @DatabaseField(columnName = "chunk_flags_enabled", canBeNull = false)
    private boolean chunkFlagsEnabled;

    @DatabaseField(columnName = "invite", canBeNull = false)
    private boolean invite;

    @DatabaseField(columnName = "change_chunk_flags", canBeNull = false)
    private boolean changeChunkFlags;

    @DatabaseField(columnName = "change_global_flags", canBeNull = false)
    private boolean changeGlobalFlags;

    @DatabaseField(columnName = "interact", canBeNull = false)
    private boolean interact;

    @DatabaseField(columnName = "build", canBeNull = false)
    private boolean build;

    @DatabaseField(columnName = "break", canBeNull = false)
    private boolean breakBlock;

    @DatabaseField(columnName = "damage", canBeNull = false)
    private boolean damage;

    @DatabaseField(columnName = "pvp", canBeNull = false)
    private boolean pvp;

    @DatabaseField(columnName = "pve", canBeNull = false)
    private boolean pve;

    @DatabaseField(columnName = "redstone", canBeNull = false)
    private boolean redstone;

    @DatabaseField(columnName = "explosion", canBeNull = false)
    private boolean explosion;

    @DatabaseField(columnName = "mob_spawning", canBeNull = false)
    private boolean mobSpawning;

    @DatabaseField(columnName = "teleport", canBeNull = false)
    private boolean teleport;

    public PermissionTable(UUID uniqueID, PermissionType permissionType) {
        this.uniqueID = uniqueID;
        this.permissionType = permissionType;
        this.chunkFlagsEnabled = false;
        this.invite = false;
        this.changeChunkFlags = false;
        this.changeGlobalFlags = false;
        this.interact = false;
        this.build = false;
        this.breakBlock = false;
        this.damage = false;
        this.pvp = false;
        this.pve = false;
        this.redstone = true;
        this.explosion = false;
        this.mobSpawning = false;
        this.teleport = false;
    }
}
