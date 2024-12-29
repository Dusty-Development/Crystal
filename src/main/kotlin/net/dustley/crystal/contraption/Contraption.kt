package net.dustley.crystal.contraption

import net.dustley.crystal.Crystal
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.scrapyard.ScrapyardPlot
import java.util.*


abstract class Contraption(
    val uuid: UUID,
    var transform: Transform,
    val plot: ScrapyardPlot,
    val contraptionManager: ContraptionManager
) {

    init {
        Crystal.LOGGER.info("Created new contraption at: ${transform.position} with id: $uuid")
    }

    /**
     * Runs every game tick
     */
    fun tick() {

    }

    /**
     * Runs every physics tick
     */
    fun physTick() {
        //TODO: remove this if it's unnecesart

        // ^ its for applying forces
    }

}