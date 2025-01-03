package net.dustley.crystal.contraption.client

import net.dustley.crystal.api.contraption.contraptionManager
import net.dustley.crystal.api.render.renderLine
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.debug.DebugRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import org.joml.Matrix4f
import org.joml.Quaternionf


//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/1.18.x/main/common/src/main/java/org/valkyrienskies/mod/mixin/client/renderer/MixinGameRenderer.java

//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/1.18.x/main/common/src/main/java/org/valkyrienskies/mod/mixin/client/renderer/MixinLevelRenderer.java

//org.valkyrienskies.mod.mixin.mod_compat.vanilla_renderer;
class ContraptionRenderSystem(val world: ClientWorld) {

    fun updateAndRender(context: WorldRenderContext) {
        val stack = context.matrixStack() ?: return
        stack.push()

        val cameraPos = context.camera().pos
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        world.contraptionManager().contraptions.forEach { (uuid, contraption) ->
            (contraption as ClientContraption)
            renderContraption(contraption, context)
            contraption.render(context, this)
        }

        stack.pop()
    }

    private fun renderContraption(contraption: ClientContraption, context: WorldRenderContext) {
        val stack = context.matrixStack() ?: return
        stack.push()

        val transform = contraption.contraptionManager.handler.getTransform(contraption.uuid)
        val position = transform.position
        stack.translate(position.x, position.y, position.z)
        stack.multiply(Quaternionf(transform.rotation))

        stack.scale(contraption.transform.scale.toFloat(), contraption.transform.scale.toFloat(), contraption.transform.scale.toFloat())

        renderChunks(contraption, stack, context)
        renderDebug(contraption, stack, context, false)
//        if(context.gameRenderer().client.debugHud.shouldShowDebugHud()) renderDebug(contraption, stack, context, true)

        stack.pop()
    }

    fun renderChunks(contraption: ClientContraption, stack: MatrixStack, context: WorldRenderContext) {

        val world: ClientWorld = context.world()
        val consumers: VertexConsumerProvider = context.consumers()!!
        val client = MinecraftClient.getInstance()
        val blockRenderManager = client.blockRenderManager
        val random: Random = Random.create()
        random.setSeed(MinecraftClient.getInstance().player!!.age.toLong())

        // For now, we make a plot at 0,0 so that testing is easy
        val plot = contraption.plot //world.contraptionManager().scrapyard.getPlot(Vector2i(0,1), true)!!
        val plotCenterBlockPos = BlockPos(plot.centerPos.x.toInt(), plot.centerPos.y.toInt(), plot.centerPos.z.toInt())

        world.chunkManager.setChunkForced(plot.centerChunkPos, true)
        world.chunkManager.isChunkLoaded(plot.centerChunkPos.x, plot.centerChunkPos.z)

        stack.push() // Push into the plot

        // Loop over the chunks and render if necessary
        plot.controlledChunkPositions.forEach { chunkPos ->
            val chunk = world.getChunk(chunkPos.x, chunkPos.z)
            if(chunk.isEmpty) return@forEach // Cancel if chunk is empty

            stack.push() // Push into the chunk

            stack.translate(0.0, (world.bottomY + world.topY) * 0.5, 0.0);

            for (yIndex in 0..< chunk.sectionArray.size) {
                stack.push() // Push into the Section

                val chunkSection = chunk.getSection(yIndex)

                if(!chunkSection.isEmpty) {

                    val sectionYBottom = chunk.sectionIndexToCoord(yIndex)

                    val minBlockPos = chunkPos.getBlockPos(0,0,0).withY(sectionYBottom)
                    val maxBlockPos = chunkPos.getBlockPos(15,0,15).withY(sectionYBottom + 15)

                    for (xPos in minBlockPos.x..maxBlockPos.x) {
                        for (zPos in minBlockPos.z..maxBlockPos.z) {
                            for (yPos in minBlockPos.y..maxBlockPos.y) {
                                val blockPos = BlockPos(xPos, yPos, zPos) // The block in world space (the very large number one)
                                val blockState = world.getBlockState(blockPos)
                                val fluidState = world.getFluidState(blockPos)
                                if(!blockState.isAir) {
                                    val renderLayer = RenderLayers.getBlockLayer(blockState)
                                    val vertexConsumer = consumers.getBuffer(renderLayer)
                                    val offsetPosition = blockPos.subtract(plotCenterBlockPos).toCenterPos()

                                    stack.push() // Push into the block

                                    stack.translate(offsetPosition.x, offsetPosition.y, offsetPosition.z)
                                    if (fluidState.isEmpty) blockRenderManager.renderBlock(blockState, blockPos, world, stack, vertexConsumer, true, random)
//                            else blockRenderManager.renderFluid(blockPos, world, vertexConsumer, blockState, fluidState)

                                    stack.pop() // Pop out of the block
                                }
                            }
                        }
                    }
                }

                stack.pop() // Pop out of the section
            }

            stack.pop() // Pop out of the chunk
        }
        stack.pop() // Pop out of the plot
    }

    fun renderDebug(contraption: ClientContraption, stack: MatrixStack, context: WorldRenderContext, renderText: Boolean) {
        stack.push()
        // Render the chunk coordinates as text
        val chunkText: Text = Text.of("UUID: ${contraption.uuid} | Plot: {${contraption.plot.plotPosition.x()}, ${contraption.plot.plotPosition.y()}}")
        val color = 0xFFFFFF // White color

        // Use Minecraft's built-in text rendering method
        val textRenderer: TextRenderer = MinecraftClient.getInstance().textRenderer
        val xOffset: Float = -textRenderer.getWidth(chunkText) / 2f

        val camPos = context.camera().pos
        val transform = contraption.transform

        val matrix = Matrix4f()
            .translate(-camPos.x.toFloat(), -camPos.y.toFloat(), -camPos.z.toFloat())
            .translate(
                transform.position.x.toFloat(),
                transform.position.y.toFloat() + 1f,
                transform.position.z.toFloat()
            )
            .rotate(RotationAxis.NEGATIVE_Y.rotationDegrees(context.camera().yaw))
            .rotate(RotationAxis.POSITIVE_X.rotationDegrees(context.camera().pitch))
            .scale(-0.025f)

        if (renderText) textRenderer.draw(chunkText, xOffset, 0f, color, false, matrix, context.consumers(), TextRenderer.TextLayerType.NORMAL, color, 255)

        val scale = 1.0
        val box = Box.of(Vec3d.ZERO, scale, scale, scale)
        val vertexConsumer = context.consumers()
        DebugRenderer.drawBox(stack, vertexConsumer, box, 0.5f,0.5f,0.5f,0.75f)

        // Draw the lines
        if (vertexConsumer != null) {
            val length = 2.0
            renderLine(vertexConsumer, stack, Vec3d.ZERO, Vec3d(length,0.0,0.0), 1f, 0f, 0f, 1f)
            renderLine(vertexConsumer, stack, Vec3d.ZERO, Vec3d(0.0,length,0.0), 0f, 1f, 0f, 1f)
            renderLine(vertexConsumer, stack, Vec3d.ZERO, Vec3d(0.0,0.0,length), 0f, 0f, 1f, 1f)
        }

        stack.pop()
    }

}