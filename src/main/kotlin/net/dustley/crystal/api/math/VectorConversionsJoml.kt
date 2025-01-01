package net.dustley.crystal.api.math

import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3f
import physx.common.PxQuat
import physx.common.PxVec3

fun Vector3d.toFloat() = Vector3f(this.x.toFloat(),this.y.toFloat(),this.z.toFloat())

fun Vector3f.toDouble() = Vector3d(this.x.toDouble(),this.y.toDouble(),this.z.toDouble())

fun Vector3d.toPx() = PxVec3(this.x.toFloat(),this.y.toFloat(),this.z.toFloat())

fun Vector3f.toPx() = PxVec3(this.x,this.y,this.z)

fun Quaterniond.toPx() = PxQuat(this.x.toFloat(), this.y.toFloat(), this.z.toFloat(), this.w.toFloat())

//fun Quaternionf.toPx() = PxQuat(this.x, this.y, this.z, this.w)