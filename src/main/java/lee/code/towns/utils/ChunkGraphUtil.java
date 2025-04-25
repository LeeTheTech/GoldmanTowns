package lee.code.towns.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkGraphUtil {

  public static boolean areChunksConnected(Set<String> chunks, Set<String> outposts, String removedChunk) {
    Map<String, Set<String>> chunkGraph = new ConcurrentHashMap<>();
    Set<String> targetChunks = ConcurrentHashMap.newKeySet();
    targetChunks.addAll(chunks);
    targetChunks.removeAll(outposts);
    targetChunks.remove(removedChunk);

    for (String chunk : targetChunks) {
      String[] chunkParts = chunk.split(",");
      int x = Integer.parseInt(chunkParts[1]);
      int z = Integer.parseInt(chunkParts[2]);

      Set<String> neighbors = ConcurrentHashMap.newKeySet();
      for (int dx = -1; dx <= 1; dx++) {
        for (int dz = -1; dz <= 1; dz++) {
          if (dx != 0 || dz != 0) {
            String neighborChunk = "world," + (x + dx) + "," + (z + dz);
            if (targetChunks.contains(neighborChunk)) neighbors.add(neighborChunk);
          }
        }
      }
      chunkGraph.put(chunk, neighbors);
    }

    Set<String> visitedChunks = ConcurrentHashMap.newKeySet();
    depthFirstSearch(chunkGraph, targetChunks.iterator().next(), visitedChunks);

    return visitedChunks.size() == targetChunks.size();
  }

  private static void depthFirstSearch(Map<String, Set<String>> graph, String currentChunk, Set<String> visitedChunks) {
    visitedChunks.add(currentChunk);
    for (String neighbor : graph.getOrDefault(currentChunk, Collections.emptySet())) {
      if (!visitedChunks.contains(neighbor)) {
        depthFirstSearch(graph, neighbor, visitedChunks);
      }
    }
  }
}
