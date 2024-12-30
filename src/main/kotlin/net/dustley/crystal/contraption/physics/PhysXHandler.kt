package net.dustley.crystal.contraption.physics

import net.dustley.crystal.Crystal.LOGGER
import net.dustley.crystal.Crystal.foundation
import net.dustley.crystal.Crystal.version
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.api.math.toCrystal
import net.dustley.crystal.api.math.toJOMLD
import net.dustley.crystal.contraption.Contraption
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Box
import net.minecraft.world.World
import physx.PxTopLevelFunctions
import physx.common.*
import physx.geometry.PxBoxGeometry
import physx.geometry.PxCustomGeometry
import physx.geometry.PxGeometryQuery
import physx.physics.*
import java.util.*
import kotlin.collections.HashMap


class PhysXHandler(threads: Int = 4, val world: World) {
    private var dispatcher : PxDefaultCpuDispatcher
    private var physics : PxPhysics
    var scene : PxScene

    val actorData : HashMap<UUID,ActorData> = hashMapOf()
    data class ActorData(val actor : PxRigidDynamic, val shape : PxShape, val material : PxMaterial)

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
        val body = physics.createRigidDynamic(contraption.transform.toPx())
        val callback: ContraptionGeometryCallback = ContraptionGeometryCallback(contraption)
        val material = physics.createMaterial(.5f, .5f, .1f)
        val shape = physics.createShape(PxCustomGeometry(callback), material)
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actorData[contraption.uuid] = ActorData(body, shape, material) //TODO: remove hotfic material
        return body
    }

    fun createBoxActor(id: UUID,  pose: Transform,  aabb: Box): PxRigidDynamic {
        val material = physics.createMaterial(.5f, .5f, .5f)
        val shape = physics.createShape(PxBoxGeometry(aabb.lengthX.toFloat(), aabb.lengthY.toFloat(), aabb.lengthZ.toFloat()), material, true, shapeFlags)
        val body = physics.createRigidDynamic(pose.toPx())
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actorData[id] = ActorData(body, shape, material)
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

                val shape = physics.createShape(PxCustomGeometry(callback),  physics.createMaterial(.5f, .5f, 0f))
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