package net.dustley.crystal.api.math

import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Quaterniond;
import org.joml.Vector3d
import physx.common.PxBounds3
import physx.common.PxQuat
import physx.common.PxTransform
import physx.common.PxVec3

fun PxTransform.toCrystal() = Transform(this.p.toJOMLD(), 1.0, this.q.toJOMLD())

//fun PxQuat.toFJOML() = Quaternionf(this.x, this.y, this.z, this.w)

fun PxQuat.toJOMLD() = Quaterniond(this.x.toDouble(), this.y.toDouble(), this.z.toDouble(), this.w.toDouble())

//fun PxVec3.toFJOML() = Vector3f(this.x, this.y, this.z)

fun PxVec3.toJOMLD() = Vector3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())

fun PxVec3.toMC() = Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())

fun PxBounds3.toMC() = Box(this.minimum.toMC(), this.maximum.toMC())