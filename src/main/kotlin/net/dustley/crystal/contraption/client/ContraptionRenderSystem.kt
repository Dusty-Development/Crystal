package net.dustley.crystal.contraption.client

import com.mojang.blaze3d.systems.RenderSystem
import net.dustley.crystal.api.contraption.contraptionManager
import net.dustley.crystal.contraption.Contraption
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.world.ClientWorld
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



        stack.pop()
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