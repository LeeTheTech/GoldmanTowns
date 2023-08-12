package lee.code.towns.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkGraphUtil {

    public static boolean areChunksConnected(Set<String> chunks, Set<String> outposts, String removedChunk) {
        final Map<String, Set<String>> chunkGraph = new ConcurrentHashMap<>();
        for (String chunk : chunks) {
            if (!chunk.equals(removedChunk)) {
                final String[] chunkParts = chunk.split(",");
                final int x = Integer.parseInt(chunkParts[1]);
                final int z = Integer.parseInt(chunkParts[2]);

                final Set<String> neighbors = ConcurrentHashMap.newKeySet();
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx != 0 || dz != 0) {
                            final String neighborChunk = "world," + (x + dx) + "," + (z + dz);
                            if (outposts.contains(neighborChunk)) continue;
                            if (!neighborChunk.equals(removedChunk) && chunks.contains(neighborChunk)) {
                                neighbors.add(neighborChunk);
                            }
                        }
                    }
                }
                chunkGraph.put(chunk, neighbors);
            }
        }

        final Set<String> visitedChunks = ConcurrentHashMap.newKeySet();
        depthFirstSearch(chunkGraph, chunks.iterator().next(), visitedChunks);

        return visitedChunks.size() == chunks.size() - (outposts.size() + 1);
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
