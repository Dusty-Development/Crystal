package net.dustley.crystal.contraption.client

import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.contraption.ContraptionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.world.World
import org.joml.Vector2i
import java.util.*

class ClientContraptionManager(val clientWorld: ClientWorld) : ContraptionManager(clientWorld as World) {
    val renderSystem = ContraptionRenderSystem(clientWorld)
    override val isClientSide: Boolean = true

    // ships are only to be created on the server.
    // This is to avoid issues when assigning a id.
    fun createAndAddContraptionFromPacket(id:UUID, transform: Transform, plotPos: Vector2i): ClientContraption {
        val plot = scrapyard.createPlot(plotPos)
        val contraption = ClientContraption(id, transform, plot, this)
        addContraption(id, contraption)

        setupContraptionPhys(contraption)

        println("CREATED CLIENT CONTRAPTION WITH COUNT: ${contraptions.size}")
        return contraption
    }

}