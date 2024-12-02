package net.dustley.contraption.client

import net.dustley.contraption.ContraptionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.world.World

class ClientContraptionManager(val clientWorld: ClientWorld) : ContraptionManager(clientWorld as World) {
    override val isClientSide: Boolean = true
}