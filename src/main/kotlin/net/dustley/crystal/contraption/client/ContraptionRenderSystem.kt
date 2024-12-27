package net.dustley.crystal.contraption.client

import com.mojang.blaze3d.systems.RenderSystem
import net.dustley.crystal.api.contraption.contraptionManager
import net.dustley.crystal.api.math.toJOML
import net.dustley.crystal.contraption.Contraption
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.random.Random
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

        renderBlocks(contraption, stack, context)
        if(context.gameRenderer().client.debugHud.shouldShowDebugHud()) renderDebugText(contraption, stack, context)

        stack.pop()
    }

    private fun renderBlocks(contraption: Contraption, stack: MatrixStack, context: WorldRenderContext) {
        stack.push()
        val world: ClientWorld = context.world()
        val consumers: VertexConsumerProvider = context.consumers()!!
        val client = MinecraftClient.getInstance()
        val blockRenderManager = client.blockRenderManager
        val random: Random = Random.create()

        val renderPos = contraption.transform.position
        val blockPos = BlockPos(renderPos.x.toInt(), renderPos.y.toInt(), renderPos.z.toInt())
        val chunkPosVec3i = blockPos.toJOML().div(16)
        val chunkPos = ChunkPos(chunkPosVec3i.x, chunkPosVec3i.z)

        stack.translate((-chunkPos.centerX).toDouble(), 0.0, (-chunkPos.centerZ).toDouble());

        for (xPos in chunkPos.startX..chunkPos.endX) {
            for (zPos in chunkPos.startZ..chunkPos.endZ) {
                for (yPos in -64 until -40) {
                    val blockPosition = BlockPos(xPos, yPos, zPos)
                    val blockState = world.getBlockState(blockPosition)

                    if(blockState.isAir) break

                    val renderLayer = RenderLayers.getBlockLayer(blockState)
                    val vertexConsumer = consumers.getBuffer(renderLayer)
                    val offsetPosition = blockPosition.subtract(blockPos).toCenterPos()

                    stack.push()
                    stack.translate(offsetPosition.x, offsetPosition.y, offsetPosition.z)
                    blockRenderManager.renderBlock(blockState, blockPosition, world, stack, vertexConsumer, true, random)

                    stack.pop()
                }
            }
        }
        stack.pop()
    }

    fun renderDebugText(contraption: Contraption, stack: MatrixStack, context: WorldRenderContext) {
        stack.push()
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
        stack.pop()
    }

    private fun setupRenderSystem() {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
//        RenderSystem.depthMask(false)
        RenderSystem.disableCull()
        RenderSystem.enableDepthTest()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
    }

    private fun resetRenderSystem() {
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
//        RenderSystem.depthMask(true)
    }
}