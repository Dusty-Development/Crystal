package net.dustley.contraption.piece

import net.dustley.api.math.Transform
import net.dustley.contraption.ContraptionManager
import net.minecraft.nbt.NbtCompound
import java.util.*

class ContraptionPiece(
    val uuid: UUID,
    val transform: Transform,
    val contraptionManager: ContraptionManager
) {

    val cacheTransform: Transform = transform.copy()

    fun loadData(data: NbtCompound) {

    }

    fun saveData() {

    }

}