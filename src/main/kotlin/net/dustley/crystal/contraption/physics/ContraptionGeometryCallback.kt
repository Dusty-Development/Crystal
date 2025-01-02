package net.dustley.crystal.contraption.physics

import net.dustley.crystal.api.math.*
import net.dustley.crystal.contraption.Contraption
import net.minecraft.block.ShapeContext
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.ShapeType
import net.minecraft.world.chunk.ChunkStatus
import physx.common.PxBounds3
import physx.common.PxTransform
import physx.common.PxVec3
import physx.geometry.PxContactBuffer
import physx.geometry.PxGeometry
import physx.geometry.SimpleCustomGeometryCallbacksImpl
import physx.physics.PxGeomRaycastHit
import physx.physics.PxHitFlags
import kotlin.math.max

class ContraptionGeometryCallback(val contraption: Contraption)
    :  SimpleCustomGeometryCallbacksImpl() {

    private val plotBounds: PxBounds3
    init {
        var min = BlockPos(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        var max = BlockPos(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
        var maxH = -63L
        for (chunkPos in contraption.plot.controlledChunkPositions) {
            val chunkHeight = contraption.contraptionManager.world.chunkManager.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false)
            if (chunkHeight != null) {
                if (chunkPos.startPos < min) min = chunkPos.startPos
                if (chunkPos.getBlockPos(15, 0, 15) > max) max = chunkPos.getBlockPos(15, 0, 15)
                for (height in chunkHeight.getHeightmap(Heightmap.Type.WORLD_SURFACE).asLongArray()) {
                    maxH = max(maxH, height)
                }
            }
        }
        plotBounds = PxBounds3(min.withY(-64).toJOMLD().toPx(), max.withY(maxH.toInt()).toJOMLD().toPx())
    }

    override fun raycastImpl(
        origin: PxVec3?,
        dir: PxVec3?,
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
            dir != null &&
//            geom != null && always based on plot
            pose != null &&
            hitFlags != null &&
            rayHits != null) {
            val  transform = pose.toCrystal().getMatrix4d().invert()
            val result = contraption.contraptionManager.world.raycast(RaycastContext(
                transform.transformPosition(origin.toJOMLD()).toMinecraft(),
                dir.toMC(),
                ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
            ))
            if(result.type  == HitResult.Type.BLOCK) {
                rayHits.normal = pose.toCrystal().getMatrix4d().transformPosition(result.side.vector.toJOMLD()).toPx()
                rayHits.distance = result.pos.distanceTo(origin.toMC()).toFloat()
                rayHits.position = origin.toMC().relativize( result.pos).normalize().toJOML().toPx()
                return 1
            }
        }
        throw NullPointerException()
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

    override fun getLocalBoundsImpl(geometry: PxGeometry?): PxBounds3 = contraption.transform.transformAABB(plotBounds.toMC()).toPx()

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