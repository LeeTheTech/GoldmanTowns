package lee.code.towns;

import lee.code.towns.enums.Color;
import lee.code.towns.enums.MonsterType;
import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
  @Getter private final Set<EntityType> monsterTypes = ConcurrentHashMap.newKeySet();
  @Getter private final ConcurrentHashMap<String, String> colors = new ConcurrentHashMap<>();

  public Data() {
    loadData();
  }

  public void loadData() {
    monsterTypes.addAll(EnumSet.allOf(MonsterType.class).stream().map(MonsterType::getEntityType).toList());
    for (Color color : Color.values()) colors.put(color.name(), color.getColor());
  }
}
