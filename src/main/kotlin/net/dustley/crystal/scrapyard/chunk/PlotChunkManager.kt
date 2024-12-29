package net.dustley.crystal.scrapyard.chunk

import net.minecraft.util.math.Box
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkStatus

class PlotChunkManager(chunks: List<ChunkPos>, world: World) {

    val aabb: Box

    init {
        var value = Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

        for (chunkP: ChunkPos in chunks) {
            world.getChunk(chunkP.x, chunkP.z, ChunkStatus.SPAWN)
        }

        aabb = value
    }

}