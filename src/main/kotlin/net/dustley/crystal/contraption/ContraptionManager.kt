package net.dustley.crystal.contraption

import net.dustley.crystal.Crystal
import net.dustley.crystal.api.math.toCrystal
import net.dustley.crystal.contraption.physics.PhysXHandler
import net.dustley.crystal.scrapyard.ScrapyardPlotManager
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import java.util.*

abstract class ContraptionManager(val world: World) {

    abstract val isClientSide:Boolean // Just here to make checking for world or client side easy

    val scrapyard = ScrapyardPlotManager(world)

    val contraptions: MutableMap<UUID, Contraption> = mutableMapOf()

    val handler: PhysXHandler = PhysXHandler(4, world)

    // GAME EVENTS \\
    init {
        Crystal.LOGGER.info("Created Contraption Manager for world: $world")
    }

    /**
     * Runs when the world closes
     */
    fun unload() {
        Crystal.LOGGER.info("Unloaded ContraptionManager for world: $world")
    }

    /**
     * Runs every game tick
     */
    fun tick() {
        handler.tick(world)
        //update contraption shape
        //do tick based events such as plant growth or breaking a block
    }

    private var mAccumulator = 0.0f
    private val mStepSize = 1.0f / 60.0f

    /**
     * Runs every physics tick
     */
    fun physTick(deltaTime: Double) {
        mAccumulator += deltaTime.toFloat()
        if(mAccumulator > mStepSize) {
            val scene = handler.scene

            mAccumulator -= mStepSize + deltaTime.toFloat()

            scene.simulate(mStepSize)

            scene.fetchResults(true)
            for (contraption: Contraption in contraptions.values) {
                val actor = handler.actorData[contraption.uuid]

                if(actor != null) {
                    contraption.transform = actor.actor.globalPose.toCrystal()
                }
            }
        }
    }
    /**
     * Sets up the physics of a new contraption
     */
    fun setupContraptionPhys(contraption: Contraption) {
        handler.createBoxActor(contraption.uuid, contraption.transform, Box(0.0, 0.0, 0.0, .5, .5, .5)
            //contraption.plot.chunkManager.aabb
        )
    }

    open fun postUpdate(deltaTime: Double, context: WorldRenderContext) {}

    fun update(deltaTime: Double, context: WorldRenderContext) {
        physTick(deltaTime)

        postUpdate(deltaTime, context)
    }

    fun addContraption(id: UUID, contraption: Contraption) = contraptions.put(id, contraption)

    /**
     * Removes a created contraption from the manager
     */
    fun removeContraption(id: UUID) = contraptions.remove(id)

    /**
     * Gets a contraption from its id
     */
    fun getContraption(id: UUID): Contraption? = contraptions[id]

    /**
     * Gets the contraption that's controlling a block position
     */
    fun getContraptionManagingPos(blockPos: BlockPos) : Contraption? {
        val scrapyardPlot = scrapyard.getPlot(ChunkPos(blockPos))
        return scrapyardPlot?.controllingContraptionPart
    }

}