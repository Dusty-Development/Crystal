package net.dustley.crystal.contraption.client

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

    fun setupContraptionPhys(contraption: Contraption) {
        val shape: PxShape = handler.createBox()
        handler.createActor(contraption.uuid, contraption.transform, shape)
    }

    var mAccumulator = 0.0f
    var mStepSize = 1.0f / 60.0f

    //TODO: unjankify this
    fun update(deltaTime: Double, context: WorldRenderContext) {
        mAccumulator += deltaTime.toFloat()
        if(mAccumulator > mStepSize) {
            val scene = handler.scene
            val actors = handler.actors

            mAccumulator -= mStepSize + deltaTime.toFloat()

            //LOGGER.info(mAccumulator.toString())

            scene.simulate(mStepSize)

            scene.fetchResults(true)
            for (contraption: Contraption in contraptions.values) {
                val actor = actors[contraption.uuid]

                if (actor != null) {
                    contraption.transform = actor.globalPose.toCrystal()

//                    LOGGER.info(contraption.transform.position.toString())
                }
            }
        }

        renderSystem.updateAndRender(deltaTime, context)
    }

}