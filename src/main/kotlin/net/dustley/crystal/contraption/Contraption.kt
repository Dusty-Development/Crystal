package net.dustley.crystal.contraption

import net.dustley.crystal.Crystal
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.scrapyard.ScrapyardPlot
import net.minecraft.nbt.NbtCompound
import java.util.*


abstract class Contraption(
    val uuid: UUID,
    val transform: Transform,
    val plot: ScrapyardPlot,
    val contraptionManager: ContraptionManager
) {

    init {
        Crystal.LOGGER.info("Created new contraption at: ${transform.position} with id: $uuid")
    }

    fun loadData(data:NbtCompound) {

    }

    fun saveData() {

    }

    fun addMultiblockPiece() {

    }

    fun removePiece() {

    }

    companion object {



//        *This is a example for creating a contraption factory*

//        // Specific factory for ExampleContraption
//        class ExampleContraptionFactory(val name: String) : ContraptionFactory<ExampleContraption> {
//            override fun create(transform: Transform): ExampleContraption {
//                return ExampleContraption(transform, name)
//            }
//        }
    }

}