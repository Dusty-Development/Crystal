package net.dustley.crystal.contraption.client

import net.dustley.crystal.Crystal.LOGGER
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.api.math.toCrystal
import net.dustley.crystal.contraption.Contraption
import net.dustley.crystal.contraption.ContraptionManager
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.world.ClientWorld
import net.minecraft.world.World
import org.joml.Vector2i
import physx.physics.PxShape
import java.util.*

class ClientContraptionManager(clientWorld: ClientWorld) : ContraptionManager(clientWorld as World) {
    private val renderSystem = ContraptionRenderSystem(clientWorld)
    override val isClientSide: Boolean = true

    // ships are only to be created on the world.
    // This is to avoid issues when assigning a id.
    fun createAndAddContraptionFromPacket(id:UUID, transform: Transform, plotPos: Vector2i): ClientContraption {
        val plot = scrapyard.createPlot(plotPos)
        val contraption = ClientContraption(id, transform, plot, this)
        addContraption(id, contraption)

        setupContraptionPhys(contraption)

        println("CREATED CLIENT CONTRAPTION WITH COUNT: ${contraptions.size}")
        return contraption
    }

    override fun postUpdate(deltaTime: Double, context: WorldRenderContext) {
        renderSystem.updateAndRender(deltaTime, context)
    }

}