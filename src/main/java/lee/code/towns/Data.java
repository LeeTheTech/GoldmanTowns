package lee.code.towns;

import lee.code.towns.enums.MonsterType;
import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class Data {

    @Getter private final Set<EntityType> monsterTypes = new HashSet<>();


    public void loadData() {
        monsterTypes.addAll(EnumSet.allOf(MonsterType.class).stream().map(MonsterType::getEntityType).toList());
    }
}
