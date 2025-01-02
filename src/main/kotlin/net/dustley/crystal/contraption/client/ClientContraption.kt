package net.dustley.crystal.contraption.client

import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.contraption.Contraption
import net.dustley.crystal.scrapyard.ScrapyardPlot
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import org.joml.Quaternionf
import java.util.*

class ClientContraption(uuid: UUID, transform: Transform, plot: ScrapyardPlot, contraptionManager: ClientContraptionManager) : Contraption(uuid, transform, plot, contraptionManager) {


    fun render(context: WorldRenderContext, renderSystem: ContraptionRenderSystem) {
        val stack = context.matrixStack() ?: return
        stack.push()

        val transform = contraptionManager.handler.getTransform(uuid)
        val position = transform.position
        stack.translate(position.x, position.y, position.z)
        stack.multiply(Quaternionf(transform.rotation))

        val scale = transform.scale.toFloat()
        stack.scale(scale, scale,  scale)

        renderSystem.renderChunks(this, stack, context)
        if(context.gameRenderer().client.debugHud.shouldShowDebugHud()) renderSystem. renderDebug(this, stack, context)

        stack.pop()
    }
}