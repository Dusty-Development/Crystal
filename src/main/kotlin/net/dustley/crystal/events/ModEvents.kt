package net.dustley.crystal.events

import net.dustley.crystal.api.contraption.contraptionManager
import net.dustley.crystal.contraption.server.ServerContraption
import net.dustley.crystal.contraption.server.ServerContraptionManager
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.world.event.GameEvent

object ModEvents {

    fun registerModEvents() {
    }

    fun registerClientModEvents() {
        // Renderer
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WorldRenderEvents.AfterTranslucent { context ->
            context.world().contraptionManager().update(context.camera().lastTickDelta.toDouble(), context)
        })
    }
}