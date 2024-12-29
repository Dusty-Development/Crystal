package net.dustley.crystal.scrapyard.chunk

import net.dustley.crystal.scrapyard.ScrapyardPlot
import net.minecraft.util.math.Box
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkStatus

class PlotChunkManager(var chunks: List<ChunkPos>, val world: World, val plot: ScrapyardPlot) {

    val aabb: Box

    init {
        val value = Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

        for (chunkP: ChunkPos in chunks) {
            world.getChunk(chunkP.x, chunkP.z, ChunkStatus.SPAWN)
        }

        aabb = value
    }

    // This function should go over every block in that section and update everything based on that 16x16x16 area
    fun updateWithData(updateData: PlotUpdate) {
        val oldMinPos = aabb.minPos
        val oldMaxPos = aabb.maxPos


    }

}