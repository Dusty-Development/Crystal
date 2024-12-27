package net.dustley.crystal.events

import net.dustley.crystal.api.contraption.contraptionManager
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

object ModEvents {

    fun registerModEvents() {
    }

    fun registerClientModEvents() {

        // Renderer
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WorldRenderEvents.AfterTranslucent { context ->
            context.world().contraptionManager().renderSystem.updateAndRender(0.05, context)
        })
    }
}