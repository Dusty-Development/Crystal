package net.dustley.crystal.contraption.client

import com.mojang.blaze3d.systems.RenderSystem
import net.dustley.crystal.api.contraption.contraptionManager
import net.dustley.crystal.contraption.Contraption
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.world.ClientWorld
import net.minecraft.text.Text
import net.minecraft.util.math.RotationAxis
import org.joml.Quaternionf


//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/1.18.x/main/common/src/main/java/org/valkyrienskies/mod/mixin/client/renderer/MixinGameRenderer.java

//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/1.18.x/main/common/src/main/java/org/valkyrienskies/mod/mixin/client/renderer/MixinLevelRenderer.java

class ContraptionRenderSystem(val world:ClientWorld) {

    fun updateAndRender(deltaTime: Double, context: WorldRenderContext) {
        val stack = context.matrixStack() ?: return

        setupRenderSystem()

        val pos = context.camera().pos
        stack.translate(-pos.x, -pos.y, -pos.z)

        world.contraptionManager().contraptions.forEach { uuid, contraption ->
            renderContraption(contraption, context)
        }

        stack.translate(pos.x, pos.y, pos.z)

        resetRenderSystem()
    }

    private fun renderContraption(contraption: Contraption, context: WorldRenderContext) {
        val stack = context.matrixStack() ?: return
        stack.push()

        val position = contraption.transform.position
        stack.translate(position.x, position.y, position.z)
        stack.multiply(Quaternionf(contraption.transform.rotation))
        stack.scale(contraption.transform.scale.toFloat(), contraption.transform.scale.toFloat(), contraption.transform.scale.toFloat())

        //TODO: Implement ship rendering

        if(context.gameRenderer().client.debugHud.shouldShowDebugHud()) renderDebugText(contraption, context)

        stack.pop()
    }

    fun renderDebugText(contraption: Contraption, context: WorldRenderContext) {

        // Render the chunk coordinates as text
        val chunkText: Text = Text.of("UUID: ${contraption.uuid} | Plot: {${contraption.plot.plotPosition.x()}, ${contraption.plot.plotPosition.y()}}")
        val color = 0xFFFFFF // White color

        // Use Minecraft's built-in text rendering method
        val textRenderer: TextRenderer = MinecraftClient.getInstance().textRenderer
        val xOffset: Float = -textRenderer.getWidth(chunkText) / 2f

        val camPos = context.camera().pos

        val matrix = contraption.transform.getMatrix4f()
            .translate(-camPos.x.toFloat(), -camPos.y.toFloat(), -camPos.z.toFloat())
            .translate(0f, 1f, 0f)
            .rotate(RotationAxis.NEGATIVE_Y.rotationDegrees(context.camera().yaw))
            .rotate(RotationAxis.POSITIVE_X.rotationDegrees(context.camera().pitch))
            .rotate(RotationAxis.NEGATIVE_Z.rotationDegrees(180f))
            .scale(0.025f)

        textRenderer.draw(chunkText, xOffset, 0f, color, false, matrix, context.consumers(), TextRenderer.TextLayerType.NORMAL, color, 15)
    }

    private fun setupRenderSystem() {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.depthMask(false)
        RenderSystem.disableCull()
        RenderSystem.enableDepthTest()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
    }

    private fun resetRenderSystem() {
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
        RenderSystem.depthMask(true)
    }
}