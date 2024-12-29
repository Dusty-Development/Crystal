package net.dustley.crystal.api.math

import net.minecraft.nbt.NbtCompound
import org.joml.Matrix4d
import org.joml.Matrix4f
import org.joml.Quaterniond
import org.joml.Vector3d
import physx.common.PxTransform

data class Transform (
    val position: Vector3d = Vector3d(),
    val scale: Double = 1.0,
    val rotation: Quaterniond = Quaterniond()
) {
    fun getMatrix4d() : Matrix4d {
        val transform = Matrix4d()
        transform.translate(position)
        transform.rotate(rotation) // No rotation
        transform.scale(scale)
        return transform
    }

    fun toPx() = PxTransform(this.position.toPx(), this.rotation.toPx())

    fun getMatrix4f() : Matrix4f { return Matrix4f(getMatrix4d()) }

    fun toNBT(): NbtCompound {
        val out = NbtCompound()
        out.putDouble("positon-x", position.x)
        out.putDouble("positon-y", position.y)
        out.putDouble("positon-z", position.z)
        out.putDouble("scale", scale)
        out.putDouble("rotation-x", rotation.x)
        out.putDouble("rotation-y", rotation.y)
        out.putDouble("rotation-z", rotation.z)
        out.putDouble("rotation-w", rotation.w)
        return out
    }
}