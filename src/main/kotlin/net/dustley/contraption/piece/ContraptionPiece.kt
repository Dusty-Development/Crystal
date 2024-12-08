package net.dustley.contraption.piece

import net.dustley.Crystal
import net.dustley.api.math.Transform
import net.dustley.contraption.Contraption
import net.dustley.contraption.ContraptionManager
import net.dustley.scrapyard.ScrapyardPlot
import net.minecraft.nbt.NbtCompound
import java.util.*

class ContraptionPiece(
    val uuid: UUID,
    val transform: Transform,
    val plot: ScrapyardPlot,
    val contraption: Contraption,
    val contraptionManager: ContraptionManager
) {

    val cacheTransform: Transform = transform.copy()

    init {
        Crystal.LOGGER.info("Created new contraption piece at: ${transform.position} with id: $uuid")
        plot.activate()
    }

    fun remove() {
        plot.deactivate()
    }

    fun loadData(data: NbtCompound) {

    }

    fun saveData() {

    }

}