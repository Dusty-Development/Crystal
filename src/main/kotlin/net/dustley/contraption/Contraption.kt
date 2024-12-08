package net.dustley.contraption

import net.dustley.Crystal
import net.dustley.api.math.Transform
import net.dustley.contraption.piece.ContraptionPiece
import net.minecraft.nbt.NbtCompound
import java.util.*


class Contraption(
    val uuid: UUID,
    val transform: Transform,
    val contraptionManager: ContraptionManager
) {



    init {
        Crystal.LOGGER.info("Created new contraption at: ${transform.position} with id: $uuid")
    }

    fun loadData(data:NbtCompound) {

    }

    fun saveData() {

    }

    fun addPiece() : ContraptionPiece {
        return ContraptionPiece(UUID.randomUUID(), transform, contraptionManager.scrapyard.getOrCreatePlot(), this, contraptionManager)
    }

    fun removePiece() {

    }

}