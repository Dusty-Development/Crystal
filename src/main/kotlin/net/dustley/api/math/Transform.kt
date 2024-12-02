package net.dustley.api.math

import org.joml.Quaterniond
import org.joml.Vector3d

data class Transform (
    val position: Vector3d = Vector3d(),
    val scale: Vector3d = Vector3d(1.0, 1.0, 1.0),
    val rotation: Quaterniond = Quaterniond()
)