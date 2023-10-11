package lee.code.towns;

import lee.code.towns.enums.Flag;
import lee.code.towns.utils.ChunkUtil;
import org.bukkit.Chunk;

import java.util.UUID;

public class TownsAPI {

  public static boolean canInteract(UUID uuid, Chunk chunk) {
    return !Towns.getInstance().getCacheManager().checkPlayerLocationFlag(uuid, ChunkUtil.serializeChunkLocation(chunk), Flag.INTERACT, true);
  }

}
