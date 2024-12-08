package net.dustley.scrapyard

import net.minecraft.util.math.ChunkPos

class ScrapyardPlot(val centerChunk: ChunkPos, val plotSize: Int) {
    val chunks: List<ChunkPos>
    var isActive: Boolean = false

    init {
        val radius = plotSize / 2
        chunks = (centerChunk.x - radius until centerChunk.x + radius).flatMap { x ->
            (centerChunk.z - radius until centerChunk.z + radius).map { z ->
                ChunkPos(x, z)
            }
        }

        activate()
    }

    fun deactivate() {
        isActive = false
    }

    fun activate() {
        isActive = true
    }
}