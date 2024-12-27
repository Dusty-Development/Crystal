package net.dustley.crystal.api.math

import net.minecraft.util.math.*
import org.joml.*

// I STRAIGHT UP STOLE THIS FROM VS2, So thank you "org.valkyrienskies.mod.common.util" and whoever wrote it.
// - dust

// region JOML

fun Vector3i.set(v: Vec3i) = also {
    x = v.x
    y = v.y
    z = v.z
}

fun Vector3d.set(v: Vec3i) = also {
    x = v.x.toDouble()
    y = v.y.toDouble()
    z = v.z.toDouble()
}

fun Vector3f.set(v: Vec3i) = also {
    x = v.x.toFloat()
    y = v.y.toFloat()
    z = v.z.toFloat()
}

fun Vector3d.set(v: Position) = also {
    x = v.x
    y = v.y
    z = v.z
}

fun Vec3i.toDoubles() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

fun Vector3ic.toBlockPos() = BlockPos(x(), y(), z())
fun Vector3dc.toMinecraft() = Vec3d(x(), y(), z())
fun Vector2i.toChunkPos() = ChunkPos(this.x, this.y)

fun Matrix4d.mul(m: Matrix4fc): Matrix4d = mul(m, this)

fun Vector2ic.toMinecraft() = ChunkPos(x(), y())

fun Vec3d.toJOML() = Vector3d().set(this)

fun Vector3d.set(v: Vec3d) = also {
    x = v.x.toDouble()
    y = v.y.toDouble()
    z = v.z.toDouble()
}

fun Vector2i.set(pos: ChunkPos) = also {
    x = pos.x
    y = pos.z
}

@JvmOverloads
fun Matrix4dc.transformDirection(v: Vec3i, dest: Vector3d = Vector3d()) = transformDirection(dest.set(v.x.toDouble(), v.y.toDouble(), v.z.toDouble()))
@JvmOverloads
fun Matrix4dc.transformDirection(dir: Direction, dest: Vector3d = Vector3d()) = transformDirection(dir.vector, dest)

fun Matrix4dc.transform(v: Vector4f) = v.also {
    it.set(
        (m00() * v.x() + m01() * v.y() + m02() * v.z() + m03() * v.w()).toFloat(),
        (m10() * v.x() + m11() * v.y() + m12() * v.z() + m13() * v.w()).toFloat(),
        (m20() * v.x() + m21() * v.y() + m22() * v.z() + m23() * v.w()).toFloat(),
        (m30() * v.x() + m31() * v.y() + m32() * v.z() + m33() * v.w()).toFloat()
    )
}

fun Matrix4dc.transformPosition(v: Vec3d): Vec3d {
    return transformPosition(v.toJOML()).toMinecraft()
}

// endregion

// region Minecraft

fun Vec3i.toJOML() = Vector3i().set(this)
fun Vec3i.toJOMLD() = Vector3d().set(this)
fun Vec3i.toJOMLF() = Vector3f().set(this)

fun Position.toJOML() = Vector3d().set(this)

fun Quaterniondc.toFloat() = Quaternionf(x(), y(), z(), w())

fun ChunkPos.toJOML() = Vector2i(this.x, this.z)
// endregion
