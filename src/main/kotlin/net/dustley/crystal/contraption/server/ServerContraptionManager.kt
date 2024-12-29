package net.dustley.crystal.contraption.server

import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.contraption.Contraption
import net.dustley.crystal.contraption.ContraptionManager
import net.dustley.crystal.network.s2c.CreateContraptionS2CPacketPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World
import org.joml.Vector2i
import java.util.*


class ServerContraptionManager(val serverWorld: ServerWorld) : ContraptionManager(serverWorld as World) {
    override val isClientSide: Boolean = false

    /**
     * Creates a new contraption on the world, and sends it to clients
     *
     * Calling on client will result in nothing done, ships are only to be created on the world.
     * This is to avoid issues when assigning a id.
     */
    fun createAndAddContraption(transform: Transform): Contraption {
        val id: UUID = UUID.randomUUID()
        val plot = scrapyard.createPlot()

        val contraption = ServerContraption(id, transform, plot, this)

        //this.setupContraptionPhys(contraption)

        addContraption(id, contraption)

        world.players.forEach {
            ServerPlayNetworking.send(
                it as ServerPlayerEntity?,
                CreateContraptionS2CPacketPayload(id, transform, Vector2i(plot.plotPosition))
            )
        }

        println("CREATED SERVER CONTRAPTION WITH COUNT: ${contraptions.size}")

        return contraption
    }


    // DATA MANAGEMENT \\
//    data class ContraptionSaveData(val data: HashMap<UUID, Pair<ScrapyardPlot, PhysXHandler.ActorData>>): PersistentState() {
//
//        override fun writeNbt(nbt: NbtCompound?, registryLookup: RegistryWrapper.WrapperLookup?): NbtCompound {
//            TODO("Not yet implemented")
//        }
//
//        companion object {
//            fun deserializer(nbt: NbtCompound, wrapper: RegistryWrapper.WrapperLookup): ContraptionSaveData {
//
//            }
//        }
//    }

}