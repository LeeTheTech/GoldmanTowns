package lee.code.towns.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.EntityType;

@AllArgsConstructor
public enum MonsterType {
  ZOMBIE(EntityType.ZOMBIE),
  SKELETON(EntityType.SKELETON),
  PHANTOM(EntityType.PHANTOM),
  CREEPER(EntityType.CREEPER),
  ENDERMAN(EntityType.ENDERMAN),
  EVOKER(EntityType.EVOKER),
  PILLAGER(EntityType.PILLAGER),
  HUSK(EntityType.HUSK),
  PIGLIN(EntityType.PIGLIN),
  PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE),
  WITHER(EntityType.WITHER),
  WITHER_SKELETON(EntityType.WITHER_SKELETON),
  SLIME(EntityType.SLIME),
  VINDICATOR(EntityType.VINDICATOR),
  SHULKER(EntityType.SHULKER),
  GHAST(EntityType.GHAST),
  BLAZE(EntityType.BLAZE),
  SILVERFISH(EntityType.SILVERFISH),
  RAVAGER(EntityType.RAVAGER),
  VEX(EntityType.VEX),
  ENDERMITE(EntityType.ENDERMITE),
  GIANT(EntityType.GIANT),
  STRIDER(EntityType.STRIDER),
  ILLUSIONER(EntityType.ILLUSIONER),
  WARDEN(EntityType.WARDEN),
  SPIDER(EntityType.SPIDER),
  CAVE_SPIDER(EntityType.CAVE_SPIDER),
  STRAY(EntityType.STRAY),
  WITCH(EntityType.WITCH)
  ;

  @Getter private final EntityType entityType;
}
