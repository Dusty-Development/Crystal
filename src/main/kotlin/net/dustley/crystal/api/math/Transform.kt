package net.dustley.crystal.api.math

import org.joml.Matrix4d
import org.joml.Matrix4f
import org.joml.Quaterniond
import org.joml.Vector3d

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

    fun getMatrix4f() : Matrix4f { return Matrix4f(getMatrix4d()) }
}