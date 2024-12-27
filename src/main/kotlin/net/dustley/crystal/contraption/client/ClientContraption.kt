package net.dustley.crystal.contraption.client

import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.contraption.Contraption
import net.dustley.crystal.scrapyard.ScrapyardPlot
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import java.util.*

class ClientContraption(uuid: UUID, transform: Transform, plot: ScrapyardPlot, contraptionManager: ClientContraptionManager) : Contraption(uuid, transform, plot, contraptionManager) {

    var mesh = null

    fun render(context: WorldRenderContext) {

    }
}