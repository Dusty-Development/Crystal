package net.dustley.crystal.api.render

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d


fun renderLine(
    vertexConsumerProvider: VertexConsumerProvider,
    matrixStack: MatrixStack,
    start: Vec3d,
    end: Vec3d,
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float,
) {
    // Push the MatrixStack transformation
    matrixStack.push()

    // Apply the current transformation matrix to the vertices
    val matrix = matrixStack.peek().positionMatrix
    val vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.LINE_STRIP)
    val norm = end.subtract(start).normalize()

    // Add the line vertices to the VertexConsumer
    vertexConsumer.vertex(matrix, start.x.toFloat(), start.y.toFloat(), start.z.toFloat()).color(red, green, blue, alpha).normal(norm.x.toFloat(), norm.y.toFloat(), norm.z.toFloat())
    vertexConsumer.vertex(matrix, end.x.toFloat(), end.y.toFloat(), end.z.toFloat()).color(red, green, blue, alpha).normal(norm.x.toFloat(), norm.y.toFloat(), norm.z.toFloat())

    // Pop the MatrixStack
    matrixStack.pop()
}