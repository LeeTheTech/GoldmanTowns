package lee.code.towns.menus.system;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MenuPlayerData {

    private UUID uuid;
    private int page;

    public MenuPlayerData(UUID uuid) {
        this.uuid = uuid;
        this.page = 0;
    }

}
