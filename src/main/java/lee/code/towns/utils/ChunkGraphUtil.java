package lee.code.towns.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkGraphUtil {

    public static boolean areChunksConnected(Set<String> chunks, Set<String> outposts, String removedChunk) {
        final Map<String, Set<String>> chunkGraph = new ConcurrentHashMap<>();
        final Set<String> targetChunks = ConcurrentHashMap.newKeySet();
        targetChunks.addAll(chunks);
        targetChunks.removeAll(outposts);
        targetChunks.remove(removedChunk);

        for (String chunk : targetChunks) {
            final String[] chunkParts = chunk.split(",");
            final int x = Integer.parseInt(chunkParts[1]);
            final int z = Integer.parseInt(chunkParts[2]);

            final Set<String> neighbors = ConcurrentHashMap.newKeySet();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx != 0 || dz != 0) {
                        final String neighborChunk = "world," + (x + dx) + "," + (z + dz);
                        if (targetChunks.contains(neighborChunk)) neighbors.add(neighborChunk);
                    }
                }
            }
            chunkGraph.put(chunk, neighbors);
        }

        final Set<String> visitedChunks = ConcurrentHashMap.newKeySet();
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
