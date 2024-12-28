package net.dustley.crystal.api.contraption

import net.dustley.mixin.accessor.ContraptionManagerAccessor
import net.dustley.crystal.contraption.ContraptionManager
import net.dustley.crystal.contraption.client.ClientContraptionManager
import net.dustley.crystal.contraption.server.ServerContraptionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

fun ClientWorld.contraptionManager(): ClientContraptionManager {
    return (this as ContraptionManagerAccessor).`crystal$getContraptionManager`() as ClientContraptionManager
}

fun ServerWorld.contraptionManager(): ServerContraptionManager {
    return (this as ContraptionManagerAccessor).`crystal$getContraptionManager`() as ServerContraptionManager
}

fun World.contraptionManager(): ContraptionManager {
    return (this as ContraptionManagerAccessor).`crystal$getContraptionManager`() as ContraptionManager
}
