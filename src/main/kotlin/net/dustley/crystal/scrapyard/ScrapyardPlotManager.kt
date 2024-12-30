package net.dustley.crystal.scrapyard

import net.dustley.crystal.Crystal
import net.dustley.crystal.api.iterator.SpiralIterator
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import org.joml.Vector2i
import kotlin.math.abs

class ScrapyardPlotManager (
    private val world: World
) {
    private val plots: HashMap<Vector2i, ScrapyardPlot> = hashMapOf()


    /**
     * Returns the plot controlling a chunk.
     *
     * you can set "createIfNull" to "true" if you would like to create a plot at that location, and one could not be found
     */
    fun getPlot(chunkPos: ChunkPos, createIfNull:Boolean = false): ScrapyardPlot? {
        val plot = plots[getPlotPosition(chunkPos)]
        if(createIfNull && plot == null) return createPlot(getPlotPosition(chunkPos))
        return plot
    }

    /**
     * Returns the plot.
     *
     * you can set "createIfNull" to "true" if you would like to create a plot at that location, and one could not be found
     */
    fun getPlot(position: Vector2i, createIfNull:Boolean = false): ScrapyardPlot? {
        val plot = plots[position]
        if(createIfNull && plot == null) return createPlot(position)
        return plot
    }

    /**
     * Creates a new plot with the specified position.
     * If a plot is already taken then throw a fake error and override it
     */
    fun createPlot(plotPos: Vector2i): ScrapyardPlot {
        val plot = plots[plotPos]

        // Log error if plot already exists
        if (plot != null) {
            Crystal.LOGGER.error("Plot already found at position: $plotPos. Attempting to override.")
        }

        val newPlot = ScrapyardPlot(plotPos, world)
        plots[plotPos] = newPlot
        return newPlot
    }

    /**
     * Creates a new plot
     */
    fun createPlot(): ScrapyardPlot {
        // Start at the edge of the void boundary
        val chunkPos = ChunkPos((VOID_DIST / CHUNK_SIZE), (VOID_DIST / CHUNK_SIZE))
        val startPos = getPlotPosition(chunkPos)

        // Iterator with custom condition
        val iterator = SpiralIterator.fromStartOnly(startPos)
        while (iterator.hasNext()) {
            val nextPos = iterator.next()
            if(!plots.containsKey(nextPos) && isPlotInScrapyard(nextPos)) {
                return createPlot(nextPos)
            }
        }

        throw IllegalArgumentException("COULD NOT FIND A PLOT FOR THE SHIP")
    }

    companion object {
        const val CHUNK_SIZE = 16 // blocks per chunk
        const val PLOT_SIZE: Int = 4 // must a f(x) = 2^x
        const val VOID_DIST: Int = 28672000

        /**
         * Returns true if a chunk is in the scrapyard
         */
        fun isChunkInScrapyard(chunkPos: ChunkPos):Boolean {
            return abs(chunkPos.x) >= VOID_DIST / CHUNK_SIZE || abs(chunkPos.z) >= VOID_DIST / CHUNK_SIZE
        }

        /**
         * Returns true if a plot is in the scrapyard
         */
        fun isPlotInScrapyard(plotPos: Vector2i):Boolean {
            return abs(plotPos.x) >= (VOID_DIST / CHUNK_SIZE) / PLOT_SIZE || abs(plotPos.y) >= (VOID_DIST / CHUNK_SIZE) / PLOT_SIZE
        }

        /**
         * Returns the plot position from a chunk position.
         */
        fun getPlotPosition(chunkPos: ChunkPos): Vector2i = Vector2i(chunkPos.x / PLOT_SIZE, chunkPos.z / PLOT_SIZE)

        /**
         * Returns the chunk position from a plot position.
         */
        fun getChunkPosition(plotPos: Vector2i): ChunkPos = ChunkPos(plotPos.x * PLOT_SIZE, plotPos.y * PLOT_SIZE)
    }

}