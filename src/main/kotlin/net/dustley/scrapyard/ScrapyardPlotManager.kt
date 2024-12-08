package net.dustley.scrapyard

import net.dustley.Crystal
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import kotlin.math.abs

class ScrapyardPlotManager (
    private val world: World,
    private val plotSize: Int = 128,
    private val voidDistance: Int = 1875128
) {
    private val plots: HashMap<ChunkPos, ScrapyardPlot> = hashMapOf()
    private var currentSpiralX = 0
    private var currentSpiralZ = 0
    private var spiralStep = 1
    private var direction = 0
    private var stepsRemaining = 1
    private var layerSteps = 0

    /**
     * Get an inactive plot or create a new one if none are available.
     */
    fun getOrCreatePlot(): ScrapyardPlot {
        // Check for an inactive plot
        val inactivePlot = plots.values.find { !it.isActive }
        if (inactivePlot != null) {
            inactivePlot.activate()
            return inactivePlot
        }

        // Create a new plot if no inactive plots are available
        return createPlot()
    }

    fun isChunkInScrapyard(chunkPos: ChunkPos):Boolean {
        return abs(chunkPos.x) > voidDistance || abs(chunkPos.z) > voidDistance
    }

    fun getPlot(chunkPos: ChunkPos): ScrapyardPlot? {
        plots.values.forEach { plot ->
            plot.chunks.forEach {
                if(it == chunkPos) return plot
            }
        }

        return null
    }

    /**
     * Create a new plot at the next spiral position.
     */
    private fun createPlot(): ScrapyardPlot {
        while (true) {
            val plotPos = ChunkPos(currentSpiralX * plotSize, currentSpiralZ * plotSize)

            // Skip if already exists
            if (plots.none { it.value.centerChunk == plotPos }) {
                val newPlot = ScrapyardPlot(plotPos, plotSize)
                newPlot.activate()
                plots[plotPos] = newPlot
                moveSpiral()
                return newPlot
            }

            // Continue moving in the spiral until an uninitialized position is found
            moveSpiral()
        }
    }


    /**
     * Move the spiral to the next position.
     */
    private fun moveSpiral() {
        when (direction) {
            0 -> currentSpiralX++ // Right
            1 -> currentSpiralZ++ // Down
            2 -> currentSpiralX-- // Left
            3 -> currentSpiralZ-- // Up
        }

        stepsRemaining--

        // If no steps remain in this direction, change direction
        if (stepsRemaining == 0) {
            direction = (direction + 1) % 4 // Rotate direction (0: right, 1: down, 2: left, 3: up)
            layerSteps++

            // Every two direction changes, increase the step count for the layer
            if (direction == 0 || direction == 2) {
                spiralStep++
            }

            stepsRemaining = spiralStep
        }
    }
}