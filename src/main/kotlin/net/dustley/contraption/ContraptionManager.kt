package net.dustley.contraption

import net.dustley.Crystal
import net.minecraft.world.World
import java.util.*

abstract class ContraptionManager(val world: World) {

    abstract val isClientSide:Boolean // Just here to make checking for server or client side easy

    val contraptions: MutableMap<UUID, Contraption> = mutableMapOf()

    // Init
    init {
        Crystal.LOGGER.info("Created ContraptionManager for world: $world")
        loadContraptions()
    }

    // Contraption

    fun getContraption(id: UUID): Contraption? = contraptions[id]

    // DataManager

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

    fun unload() {
        saveContraptions()

        Crystal.LOGGER.info("UnLoaded ContraptionManager for world: $world")
    }

}