package net.dustley.crystal.api.math

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4d
import org.joml.Matrix4f
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector4d
import org.joml.Vector4f
import physx.common.PxTransform
import kotlin.math.max
import kotlin.math.min

data class Transform (
    var position: Vector3d = Vector3d(),
    val scale: Double = 1.0,
    val rotation: Quaterniond = Quaterniond()
) {
    fun getMatrix4d() : Matrix4d {
        val transform = Matrix4d()
        transform.translate(position)
        transform.rotate(rotation) // No rotation   //??????
        transform.scale(scale)
        return transform
    }

    fun transformAABB(aabb: Box): Box {
        //              c -  max
        //           / |       / |
        //        a -|- b    |
        //        |     e -|- f
        //        | /        | /
        //     min - - h

        var min = Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)
        var max = Vector3d(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE)
        for (v in listOf(
            this.transformJOML3d(Vector3d(aabb.minX, aabb.maxY,aabb.minZ)),    //a
             this.transformJOML3d(Vector3d(aabb.minX, aabb.maxY, aabb.maxZ)),//b
             this.transformJOML3d(Vector3d(aabb.maxX, aabb.maxY,aabb.minZ)), //c
             this.transformJOML3d(aabb.maxPos.toJOML()),                                                   //max
             this.transformJOML3d(aabb.minPos.toJOML()),                                                     //min
             this.transformJOML3d(Vector3d(aabb.minX, aabb.minY, aabb.maxZ)),  //h
             this.transformJOML3d(Vector3d(aabb.maxX, aabb.minY,aabb.minZ)),   //e
             this.transformJOML3d(Vector3d(aabb.maxX, aabb.minY, aabb.maxZ)) //f
        )) {
            min = Vector3d(min(min.x, v.x), min(min.y, v.y), min(min.z, v.z))
            max = Vector3d(max(max.x, v.x), max(max.y, v.y), max(max.z, v.z))
        }

        return Box(min.toMinecraft(),  max.toMinecraft())
    }

    fun transformJOML3d(v: Vector3d): Vector3d {
       val four = this.getMatrix4d().transform(Vector4d(v, 1.0))
        return Vector3d(four.x, four.y, four.z)
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