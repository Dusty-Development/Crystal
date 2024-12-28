package net.dustley.crystal.api.math

import org.joml.Quaterniond;
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f
import physx.common.PxQuat
import physx.common.PxTransform
import physx.common.PxVec3

fun Quaterniond.toPx() = PxQuat(this.x.toFloat(), this.y.toFloat(), this.z.toFloat(), this.w.toFloat())

//fun Quaternionf.toPx() = PxQuat(this.x, this.y, this.z, this.w)

fun PxTransform.toCrystal() = Transform(this.p.toDJOML(), 1.0, this.q.toDJOML())

//fun PxQuat.toFJOML() = Quaternionf(this.x, this.y, this.z, this.w)

fun PxQuat.toDJOML() = Quaterniond(this.x.toDouble(), this.y.toDouble(), this.z.toDouble(), this.w.toDouble())

//fun PxVec3.toFJOML() = Vector3f(this.x, this.y, this.z)

fun PxVec3.toDJOML() = Vector3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())