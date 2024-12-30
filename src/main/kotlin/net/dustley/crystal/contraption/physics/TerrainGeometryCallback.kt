package net.dustley.crystal.contraption.physics

import net.dustley.crystal.api.math.toJOML
import net.dustley.crystal.api.math.toJOMLD
import net.dustley.crystal.api.math.toMC
import net.dustley.crystal.api.math.toPx
import net.minecraft.block.ShapeContext
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.ShapeType
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import physx.common.PxBounds3
import physx.common.PxTransform
import physx.common.PxVec3
import physx.geometry.PxContactBuffer
import physx.geometry.PxGeometry
import physx.geometry.SimpleCustomGeometryCallbacksImpl
import physx.physics.PxGeomRaycastHit
import physx.physics.PxHitFlags
import kotlin.math.max

class TerrainGeometryCallback(val world: World, val chunks: List<Chunk>)
    :  SimpleCustomGeometryCallbacksImpl() {

        val bounds: PxBounds3
        init {
            var min = BlockPos(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
            var max = BlockPos(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
            var maxH = -63L
            for (chunk in chunks) {
                if( chunk.pos.startPos < min) min = chunk.pos.startPos
                if( chunk.pos.getBlockPos(15, 0, 15) >  max) max = chunk.pos.getBlockPos(15, 0, 15)
                for(height in chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).asLongArray()) {
                    maxH = max(maxH, height)
                }
            }
            bounds = PxBounds3(min.withY(-64).toJOMLD().toPx(), max.withY(maxH.toInt()).toJOMLD().toPx())
        }

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
        if(
            origin != null &&
            unitDir != null &&
            //geom != null && geom is always world
            //pose != null && pose is always identity
            hitFlags != null &&
            rayHits != null) {
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
                return 1
            }
        }
        return 0
    }

    override fun generateContactsImpl( //TODO
        v1: PxGeometry?, //our geometry (not needed as we use world data)
        geom1: PxGeometry?,
        v2: PxTransform?, //our geometry pose (always identity as we use world data)
        pose1: PxTransform?,
        contactDistance: Float,
        meshContactMargin: Float,
        toleranceLength: Float,
        contactBuffer: PxContactBuffer?
    ): Boolean {
        if(geom1 == null || pose1 == null || contactBuffer == null) return false

        return true
    }

    override fun getLocalBoundsImpl(geometry: PxGeometry?): PxBounds3 = bounds

//    override fun overlapImpl( //TODO: redo after ContraptionGeometry is done
//        geom0: PxGeometry?,
//        pose0: PxTransform?,
//        geom1: PxGeometry?,
//        pose1: PxTransform?
//    ): Boolean {
//        return super.overlapImpl(geom0, pose0, geom1, pose1)
//        //TODO: implement Seperating Axis Test algorithm for cube and Contraption testing
//    }

    //TODO: do when i have enough time to do this menial ass optimization
//    override fun sweepImpl(
//        unitDir: PxVec3?,
//        maxDist: Float,
//        geom0: PxGeometry?,
//        pose0: PxTransform?,
//        geom1: PxGeometry?,
//        pose1: PxTransform?,
//        sweepHit: PxGeomSweepHit?,
//        hitFlags: PxHitFlags?,
//        inflation: Float
//    ): Boolean {
//        return super.sweepImpl(unitDir, maxDist, geom0, pose0, geom1, pose1, sweepHit, hitFlags, inflation)
//    }

}