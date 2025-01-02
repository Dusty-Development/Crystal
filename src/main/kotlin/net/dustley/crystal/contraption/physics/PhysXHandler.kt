package net.dustley.crystal.contraption.physics

import net.dustley.crystal.Crystal.LOGGER
import net.dustley.crystal.Crystal.foundation
import net.dustley.crystal.Crystal.version
import net.dustley.crystal.api.math.*
import net.dustley.crystal.contraption.Contraption
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkStatus
import org.joml.Vector3d
import physx.PxTopLevelFunctions
import physx.common.*
import physx.geometry.PxBoxGeometry
import physx.geometry.PxCustomGeometry
import physx.geometry.PxGeometryQuery
import physx.physics.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max


class PhysXHandler(threads: Int = 4, val world: World) {
    private var dispatcher : PxDefaultCpuDispatcher
    private var physics : PxPhysics
    var scene : PxScene

    val actorData : HashMap<UUID,ActorData> = hashMapOf()
    data class ActorData(val offset: Vector3d, val actor : PxRigidDynamic, val shape : PxShape, val material : PxMaterial)

    private var terrainData : HashMap<PlayerEntity, TerrainData> = hashMapOf()
    data class TerrainData(val geometry : TerrainGeometryCallback, var shape : PxShape, val actor : PxRigidStatic)

    private val filterData = PxFilterData(1, 1, 0, 0)
    val shapeFlags = PxShapeFlags((PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value or PxShapeFlagEnum.eSIMULATION_SHAPE.value).toByte())

    init {
        val tolerances = PxTolerancesScale()
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances)

        dispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(threads)

        // create a physics scene
        val sceneDesc = PxSceneDesc(tolerances)
        sceneDesc.gravity = PxVec3(0f, -9.8f, 0f)
        sceneDesc.cpuDispatcher = dispatcher
        sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
        scene = physics.createScene(sceneDesc)

        //cleanup
        sceneDesc.destroy()
        tolerances.destroy()
    }

    fun createActor(contraption: Contraption): PxRigidDynamic {
        //val callback = ContraptionGeometryCallback(contraption)
        val material = physics.createMaterial(.5f, .5f, .1f)
        //val shape = physics.createShape(PxCustomGeometry(callback), material)
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
        contraption.transform.position = contraption.transform.position.add(min.toJOMLD())
        val body = physics.createRigidDynamic(contraption.transform.toPx())
        val aabb = Box(min.withY(-64).toDoubles(), max.withY(maxH.toInt()).toDoubles().add(Vec3d(1.0,1.0,1.0)))
        val shape = physics.createShape(PxBoxGeometry(aabb.lengthX.toFloat(), aabb.lengthY.toFloat(), aabb.lengthZ.toFloat()), material)
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actorData[contraption.uuid] = ActorData(min.toJOMLD(), body, shape, material)
        return body
    }

    fun createBoxActor(id: UUID,  pose: Transform,  aabb: Box): PxRigidDynamic {
        val material = physics.createMaterial(.5f, .5f, .5f)
        val shape = physics.createShape(PxBoxGeometry(aabb.lengthX.toFloat(), aabb.lengthY.toFloat(), aabb.lengthZ.toFloat()), material, true, shapeFlags)
        val body = physics.createRigidDynamic(pose.toPx())
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actorData[id] = ActorData(Vector3d(0.0, 0.0, 0.0),body, shape, material)
        return body
    }

    fun release() {
        for(data in actorData.values) {
            data.material.destroy()
            data.shape.release()
            data.actor.release()
        }
        for (data  in terrainData.values) {
            data.geometry.destroy()
            data.shape.release()
            data.actor.release()
        }
        shapeFlags.destroy()
        filterData.destroy()
        scene.release()
        physics.release()
        dispatcher.destroy()
    }

    fun tick() {
        for(player in this.world.players) {
            terrainData.computeIfAbsent(player) { a ->
                val actor = physics.createRigidStatic(Transform(player.blockPos.withY(-61).toJOMLD()).toPx())
                val callback = TerrainGeometryCallback(this.world, listOf())

                val shape = physics.createShape(
                    //PxCustomGeometry(callback),
                    PxBoxGeometry(100f, 1f, 100f),
                    physics.createMaterial(.5f, .5f, 0f))
                shape.simulationFilterData = filterData
                actor.attachShape(shape)
                scene.addActor(actor)
                TerrainData(callback, shape,  actor)
            }

//            val actor = data.actor
//
//            actor.setGlobalPose(Transform(player.blockPos.withY(-61).toJOMLD()).toPx(), true)
        }
    }

    fun getTransform(id: UUID): Transform {
        val actor = actorData.computeIfAbsent(id) { a ->
            LOGGER.info("shit broke man :)")
            throw NullPointerException()
        }

        return actor.actor.globalPose.toCrystal()
    }

}