package net.dustley.contraption.server

import net.dustley.api.math.Transform
import net.dustley.contraption.Contraption
import net.dustley.contraption.ContraptionManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World
import java.util.*

class ServerContraptionManager(val serverWorld: ServerWorld) : ContraptionManager(serverWorld as World) {
    override val isClientSide: Boolean = false

    // Calling on client will result in nothing done,
    // ships are only to be created on the server.
    // This is to avoid issues when assigning a id.
    fun createContraption(transform: Transform): Contraption {
        val id: UUID = UUID.randomUUID()
        val contraption = Contraption(id, transform, this)
        contraptions[id] = contraption

        //TODO: add a packet to send new contraption back to clients
        return contraption
    }
}