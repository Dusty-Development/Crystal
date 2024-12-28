package net.dustley.crystal.contraption.client

import net.dustley.crystal.api.contraption.contraptionManager
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.random.Random
import org.joml.Quaternionf
import org.joml.Vector2i


//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/1.18.x/main/common/src/main/java/org/valkyrienskies/mod/mixin/client/renderer/MixinGameRenderer.java

//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/1.18.x/main/common/src/main/java/org/valkyrienskies/mod/mixin/client/renderer/MixinLevelRenderer.java

class ContraptionRenderSystem(val world: ClientWorld) {

    fun updateAndRender(deltaTime: Double, context: WorldRenderContext) {
        val stack = context.matrixStack() ?: return
        stack.push()

        val cameraPos = context.camera().pos
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        world.contraptionManager().contraptions.forEach { uuid, contraption ->
            (contraption as ClientContraption)
            renderContraption(contraption, context)
            contraption.render(context)
        }

        stack.pop()
    }

    private fun renderContraption(contraption: ClientContraption, context: WorldRenderContext) {
        val stack = context.matrixStack() ?: return
        stack.push()

        val transform = contraption.contraptionManager.handler.fetch(contraption.uuid)
        val position = transform.position
        stack.translate(position.x, position.y, position.z)
        stack.multiply(Quaternionf(transform.rotation))

        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().player!!.age / 4f))
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MinecraftClient.getInstance().player!!.age / 3f))
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MinecraftClient.getInstance().player!!.age / 1.5f))

        stack.scale(contraption.transform.scale.toFloat(), contraption.transform.scale.toFloat(), contraption.transform.scale.toFloat())

        renderChunks(contraption, stack, context)
        if(context.gameRenderer().client.debugHud.shouldShowDebugHud()) renderDebugText(contraption, stack, context)

        stack.pop()
    }

    private fun renderChunks(contraption: ClientContraption, stack: MatrixStack, context: WorldRenderContext) {

        val world: ClientWorld = context.world()
        val consumers: VertexConsumerProvider = context.consumers()!!
        val client = MinecraftClient.getInstance()
        val blockRenderManager = client.blockRenderManager
        val random: Random = Random.create()

        // For now, we make a plot at 0,0 so that testing is easy
        val plot = world.contraptionManager().scrapyard.getPlot(Vector2i(0,0), true)!!
        val plotCenterBlockPos = BlockPos(plot.centerPos.x.toInt(), plot.centerPos.y.toInt(), plot.centerPos.z.toInt())

        stack.push() // Push into the plot

        // Loop over the chunks and render if necessary
        plot.controlledChunkPositions.forEach { chunkPos ->
            val chunk = world.getChunk(chunkPos.x, chunkPos.z)
            if(chunk.isEmpty) return@forEach // Cancel if chunk is empty

            stack.push() // Push into the chunk

            stack.translate(0.0, (world.bottomY + world.topY) * 0.5, 0.0);

            stack.push() // Push into the Section

            val sectionYBottom = world.bottomY
            val sectionYTop = world.topY

            val minBlockPos = chunkPos.getBlockPos(0,0,0).withY(sectionYBottom)
            val maxBlockPos = chunkPos.getBlockPos(15,0,15).withY(sectionYTop)

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
                            if (fluidState.isEmpty) blockRenderManager.renderBlock(
                                blockState,
                                blockPos,
                                world,
                                stack,
                                vertexConsumer,
                                true,
                                random
                            )
                            blockRenderManager.renderDamage(blockState, blockPos, world, stack, vertexConsumer)
//                        if(world.getBlockEntity(blockPos) != null) blockRenderManager.renderBlockAsEntity(blockState, stack, consumers, world.getLightLevel(blockPos), 0)
//                        else
//                            blockRenderManager.renderFluid(blockPos, world, vertexConsumer, blockState, fluidState)

                            stack.pop() // Pop out of the block
                        }
                    }
                }
            }

            stack.pop() // Pop out of the section
            stack.pop() // Pop out of the chunk
        }
        stack.pop() // Pop out of the plot
    }

//            // Do some section math so that we don't need to render as many blocks
//            for (sectionIndex in chunk.sectionArray.indices) { // chunk.sectionArray.indices
//                val section = chunk.getSection(sectionIndex)
//
//                if(section.isEmpty) break // Cancel if the vertical slice is empty
//
//                stack.push() // Push into the Section
//
//                val sectionYBottom = chunk.bottomY + (sectionIndex * 16)
//                val sectionYTop = sectionYBottom + 16
//
//                val minBlockPos = chunkPos.getBlockPos(0,0,0).withY(sectionYBottom)
//                val maxBlockPos = chunkPos.getBlockPos(15,0,15).withY(sectionYTop)
//
//                for (xPos in minBlockPos.x..maxBlockPos.x) {
//                    for (zPos in minBlockPos.z..maxBlockPos.z) {
//                        for (yPos in minBlockPos.y..maxBlockPos.y) {
//
//                            val blockPos = BlockPos(xPos, yPos, zPos) // The block in world space (the very large number one)
//                            val blockState = world.getBlockState(blockPos)
//
//                            if(blockState.isAir) break // Skip air blocks
//
//                            val renderLayer = RenderLayers.getBlockLayer(blockState)
//                            val vertexConsumer = consumers.getBuffer(renderLayer)
//                            val offsetPosition = blockPos.subtract(plotCenterBlockPos).toCenterPos()
//
//                            stack.push() // Push into the block
//
//                            stack.translate(offsetPosition.x, offsetPosition.y, offsetPosition.z)
//                            blockRenderManager.renderBlock(blockState, blockPos, world, stack, vertexConsumer, true, random)
//
//                            stack.pop() // Pop out of the block
//
//                        }
//                    }
//                }
//                stack.pop() // Pop out of the section
//            }

//    private fun afterSetup(worldRenderContext: WorldRenderContext) {
//        val world = worldRenderContext.world()
//        val consumers = worldRenderContext.consumers()
//        val client = MinecraftClient.getInstance()
//        val blockRenderManager = client.blockRenderManager
//        val random = Random.create()
//        val matrixStack = MatrixStack()
//
//        matrixStack.push()
//
//        val camera = worldRenderContext.camera()
//        matrixStack.translate(camera.pos.negate())
//
//        val blockPos = BlockPos(0, -55, 0)
//        val chunkPos = ChunkPos.fromRegion(blockPos.x, blockPos.z)
//
//        val vec3dWithView = Vec3d(blockPos)
//        matrixStack.translate(vec3dWithView)
//        matrixStack.translate(0f, 1f, 0f)
//        matrixStack.translate(0.0, MathHelper.sin(client.player!!.age / 10f) * 0.2, 0.0)
//        matrixStack.scale(0.2f, 0.2f, 0.2f)
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(client.player!!.age / 4f))
//        matrixStack.translate(-chunkPos.centerX.toFloat(), 0f, -chunkPos.centerZ.toFloat())
//
//        for (xPos in chunkPos.startX..chunkPos.endX) {
//            for (zPos in chunkPos.startZ..chunkPos.endZ) {
////                for (int yPos = -64; yPos < 319; yPos++) {
//                for (yPos in -64 until -40) {
//                    val blockPos1 = BlockPos(xPos, yPos, zPos)
//                    val blockState = world.getBlockState(blockPos1)
//                    val renderLayer = RenderLayers.getBlockLayer(blockState)
//                    val buffer = consumers!!.getBuffer(renderLayer)
//                    val vec3d = Vec3d(blockPos1.subtract(blockPos))
//                    matrixStack.push()
//                    matrixStack.translate(vec3d)
//                    blockRenderManager.renderBlock(blockState, blockPos1, world, matrixStack, buffer, true, random)
//                    matrixStack.pop()
//                }
//            }
//        }
//    }

    fun renderDebugText(contraption: ClientContraption, stack: MatrixStack, context: WorldRenderContext) {
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
            .rotate(RotationAxis.NEGATIVE_Y.rotationDegrees(context.camera().yaw))
            .rotate(RotationAxis.POSITIVE_X.rotationDegrees(context.camera().pitch))
            .rotate(RotationAxis.NEGATIVE_Z.rotationDegrees(180f))
            .scale(0.025f)


        textRenderer.draw(chunkText, xOffset, 0f, color, false, matrix, context.consumers(), TextRenderer.TextLayerType.NORMAL, color, 15)
        stack.pop()
    }

}