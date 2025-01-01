package net.dustley;

import net.dustley.crystal.scrapyard.ScrapyardPlot;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.math.ChunkPos;

import java.util.function.Consumer;

public class ChunkFilterPlot implements ChunkFilter {
    ScrapyardPlot plot;

    public ChunkFilterPlot(ScrapyardPlot plotRef) {
        plot = plotRef;
    }

    public boolean isWithinDistance(int x, int z, boolean includeEdge) {
        return true;
    }

    public void forEach(Consumer<ChunkPos> consumer) {
        for (ChunkPos chunkPosition : plot.getControlledChunkPositions()) {
            consumer.accept(chunkPosition);
        }
    }
}