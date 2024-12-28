package net.dustley.crystal.contraption

import net.dustley.crystal.Crystal
import net.dustley.crystal.contraption.physics.PhysXHandler
import net.dustley.crystal.scrapyard.ScrapyardPlotManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import physx.PxTopLevelFunctions
import physx.physics.PxShape
import java.util.*

abstract class ContraptionManager(val world: World) {

    abstract val isClientSide:Boolean // Just here to make checking for server or client side easy

    val scrapyard = ScrapyardPlotManager(world)

    val contraptions: MutableMap<UUID, Contraption> = mutableMapOf()

    val handler: PhysXHandler = PhysXHandler(2, world)

    // GAME EVENTS \\
    init {
        Crystal.LOGGER.info("Created Contraption Manager for world: $world")
        loadContraptions()
    }

    /**
     * Runs when the world closes
     */
    fun unload() {
        saveContraptions()

        Crystal.LOGGER.info("UnLoaded ContraptionManager for world: $world")
    }

    /**
     * Runs every game tick
     */
    fun tick() {

    }

    /**
     * Runs every physics tick
     */
    fun physTick(double: Double) {

    }
    /**
     * Sets up the physics of a new contraption
     */
//    open fun setupContraptionPhys(contraption: Contraption) {
//
//    }

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

    // DATA MANAGEMENT \\
    fun saveContraptions() {
//        val data = NbtCompound()
//        contraptions.forEach { (id, contraption) ->
//            data.put(id.toString(), contraption.toNbt())
//        }
//        world.persistentStateManager.getOrCreate(
//            { ContraptionSaveData(data) },
//            "contraptions"
//        ).markDirty()
    }

    fun loadContraptions() {
//        val savedData = world.persistentStateManager.get<ContraptionSaveData>("contraptions")
//        savedData?.data?.keys?.forEach { key ->
//            val id = UUID.fromString(key)
//            val contraption = Contraption.fromNbt(savedData.data.getCompound(key))
//            contraptions[id] = contraption
//        }
    }

}