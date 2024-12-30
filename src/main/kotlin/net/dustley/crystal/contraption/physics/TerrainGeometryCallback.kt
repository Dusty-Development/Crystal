package net.dustley.crystal.contraption.physics

import net.dustley.crystal.api.math.toJOML
import net.dustley.crystal.api.math.toJOMLD
import net.dustley.crystal.api.math.toMC
import net.dustley.crystal.api.math.toPx
import net.minecraft.block.ShapeContext
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.ShapeType
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkManager
import physx.common.PxTransform
import physx.common.PxVec3
import physx.geometry.PxGeometry
import physx.geometry.SimpleCustomGeometryCallbacksImpl
import physx.physics.PxGeomRaycastHit
import physx.physics.PxHitFlags

class TerrainGeometryCallback(val world: World, chunkManager: ChunkManager)
    :  SimpleCustomGeometryCallbacksImpl() {

    override fun raycastImpl(
        origin: PxVec3?,
        unitDir: PxVec3?,
        geom: PxGeometry?,
        pose: PxTransform?,
        maxDist: Float,
        hitFlags: PxHitFlags?,
        maxHits: Int,
        rayHits: PxGeomRaycastHit?,
        stride: Int
    ): Int {
        if(origin != null && unitDir != null && geom != null && pose != null && hitFlags != null && rayHits != null) {
            val ctx = RaycastContext(
                origin.toMC(),
                origin.toMC().add(unitDir.toMC().multiply(maxDist.toDouble())),
                ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent())
           val result =  world.raycast(ctx)
            if (result.type !=  HitResult.Type.BLOCK ) {
                rayHits.normal = result.side.vector.toJOMLD().toPx() //TODO: add MC to Px
                rayHits.distance = result.pos.distanceTo(origin.toMC()).toFloat()
                rayHits.position = result.pos.toJOML().toPx()
                return 0
            }
            return 1
        }
        return 0
    }


}