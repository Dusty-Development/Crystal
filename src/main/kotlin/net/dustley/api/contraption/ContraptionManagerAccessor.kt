package net.dustley.api.contraption

import net.dustley.acessor.ContraptionManagerAccessor
import net.dustley.contraption.ContraptionManager
import net.dustley.contraption.client.ClientContraptionManager
import net.dustley.contraption.server.ServerContraptionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

fun ClientWorld.contraptionManager(): ClientContraptionManager {
    return (this as ContraptionManagerAccessor).contraptionManager as ClientContraptionManager
}

fun ServerWorld.contraptionManager(): ServerContraptionManager {
    return (this as ContraptionManagerAccessor).contraptionManager as ServerContraptionManager
}

fun World.contraptionManager(): ContraptionManager {
    return (this as ContraptionManagerAccessor).contraptionManager as ContraptionManager
}
