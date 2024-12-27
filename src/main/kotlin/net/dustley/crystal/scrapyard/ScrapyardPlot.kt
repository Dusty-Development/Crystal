package net.dustley.crystal.scrapyard

import net.dustley.crystal.Crystal
import net.dustley.crystal.api.math.toChunkPos
import net.dustley.crystal.api.math.toJOML
import net.dustley.crystal.api.math.toJOMLD
import net.dustley.crystal.contraption.Contraption
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import org.joml.Vector2i
import org.joml.Vector2ic
import org.joml.Vector3d

class ScrapyardPlot(
    val plotPosition: Vector2ic,
    val world: World
) {

    val centerPos:Vector3d
    val centerChunkPos:ChunkPos
    val scrapyardAABB:Box
    val controlledChunkPositions: List<ChunkPos>
    var controllingContraptionPart: Contraption? = null

    init {
        val halfSideSize = PLOT_SIZE / 2

        // Generate chunk positions within the plot bounds
        controlledChunkPositions = List(PLOT_SIZE * PLOT_SIZE) { index ->
            val x = plotPosition.x() * PLOT_SIZE + (index / PLOT_SIZE)
            val y = plotPosition.y() * PLOT_SIZE + (index % PLOT_SIZE)
            ChunkPos(x, y)
        }

        // AABB generation
        val minScrapyardBlock = plotToBlockPos(Vector2i(plotPosition)).add(0,world.bottomY,0)
        val maxScrapyardBlock = plotToBlockPos(Vector2i(plotPosition).add(1,1)).add(-1,world.topY,-1)
        scrapyardAABB = Box.enclosing(minScrapyardBlock, maxScrapyardBlock)

        Crystal.LOGGER.info("Created plot from blocks ($minScrapyardBlock) to ($maxScrapyardBlock)")

        // Center positions
        centerChunkPos = plotToChunkPos(Vector2i(plotPosition)).toJOML().add(halfSideSize, halfSideSize).toChunkPos()
        centerPos = scrapyardAABB.center.toJOML()
    }

    companion object {
        const val CHUNK_SIZE = ScrapyardPlotManager.CHUNK_SIZE // blocks per chunk
        const val PLOT_SIZE: Int = ScrapyardPlotManager.PLOT_SIZE

        /**
         * Returns the chunk position at the corner of the plot.
         */
        fun plotToChunkPos(plotPos:Vector2i):ChunkPos = ChunkPos(plotPos.x * ScrapyardPlotManager.PLOT_SIZE, plotPos.y * ScrapyardPlotManager.PLOT_SIZE)

        /**
         * Returns the block position at the corner of the plot.
         * The height always returns 0!
         */
        fun plotToBlockPos(plotPos:Vector2i):BlockPos = BlockPos(plotToChunkPos(plotPos).x * CHUNK_SIZE, 0, plotToChunkPos(plotPos).z * CHUNK_SIZE)

        /**
         * Returns the vector position at the corner of the plot.
         * The height always returns 0!
         */
        fun plotToWorldPos(plotPos:Vector2i):Vector3d = plotToBlockPos(plotPos).toJOMLD()

    }

}