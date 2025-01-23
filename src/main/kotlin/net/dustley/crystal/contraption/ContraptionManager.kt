package net.dustley.crystal.contraption

import net.dustley.crystal.Crystal
import net.dustley.crystal.api.math.*
import net.dustley.crystal.contraption.physics.PhysXHandler
import net.dustley.crystal.scrapyard.ScrapyardPlotManager
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Vector3d
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
        contraptions.values.forEach { it.unload() }
        contraptions.clear()

        handler.release()
        Crystal.LOGGER.info("Unloaded ContraptionManager for world: $world")
    }

    /**
     * Runs every game tick
     */
    fun tick() {
        handler.tick()
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

            // Apply forces
            for (contraption: Contraption in contraptions.values) {
                val actor = handler.actorData[contraption.uuid]

                if(actor != null) {
                    if (contraption.transform.position.y <= -53) {
                        actor.actor.addForce(Vector3d(0.0, actor.actor.mass.toDouble() * 10.8, 0.0).toPx())
                    } else {
                        actor.actor.addForce(Vector3d(0.0, actor.actor.mass.toDouble() * 9.0, 0.0).toPx())
                    }

                    actor.actor.addForce(actor.actor.linearVelocity.toJOMLD().mul(-0.1).toPx())
                }
            }

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


    fun getSquaredDistanceWithContraptions(positionA: Vec3d, positionB: Vec3d):Double = translatePositionToWorld(positionA).squaredDistanceTo(translatePositionToWorld(positionB))
    fun translatePositionToWorld(position: Vec3d):Vec3d {
        val blockPos = BlockPos.ofFloored(position)
        val isInScrapyard = ScrapyardPlotManager.isChunkInScrapyard(ChunkPos(BlockPos.ofFloored(position)))
        if(!isInScrapyard) return position

        val contraption = getContraptionManagingPos(blockPos) ?: return position

        val localPos = contraption.plot.centerPos.sub(position.toJOML(),Vector3d())
        val worldPos = contraption.transform.transformJOML3d(localPos)

        return worldPos.toMinecraft()
    }

}