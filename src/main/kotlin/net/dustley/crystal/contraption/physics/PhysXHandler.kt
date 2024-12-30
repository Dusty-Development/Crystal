package net.dustley.crystal.contraption.physics

import net.dustley.crystal.Crystal.LOGGER
import net.dustley.crystal.Crystal.foundation
import net.dustley.crystal.Crystal.version
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.api.math.toCrystal
import net.dustley.crystal.api.math.toJOML
import net.dustley.crystal.api.math.toJOMLD
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Box
import net.minecraft.world.World
import org.joml.Vector3d
import physx.PxTopLevelFunctions
import physx.common.*
import physx.geometry.PxBoxGeometry
import physx.geometry.PxCustomGeometry
import physx.geometry.PxGeometry
import physx.physics.*
import java.util.*
import kotlin.collections.HashMap


class PhysXHandler(threads: Int = 4, world: World) {
    private var dispatcher : PxDefaultCpuDispatcher
    private var physics : PxPhysics
    var scene : PxScene

    val actors : HashMap<UUID,ActorData> = hashMapOf()
    data class ActorData(val actor : PxRigidDynamic, val shape : PxShape, val material : PxMaterial)

    private var terrainData : HashMap<PlayerEntity, TerrainData> = hashMapOf()
    data class TerrainData(val geometry : TerrainGeometry, var shape : PxShape, val actor : PxRigidStatic)

    private val filterData = PxFilterData(1, 1, 0, 0)
    val shapeFlags = PxShapeFlags((PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value or PxShapeFlagEnum.eSIMULATION_SHAPE.value).toByte())

    init {
        if(foundation == null) {
            LOGGER.info("null")
        }
        val tolerances = PxTolerancesScale()
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances);

        dispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(threads)

        // create a physics scene
        val sceneDesc = PxSceneDesc(tolerances)
        sceneDesc.gravity = PxVec3(0f, -9.8f, 0f)
        sceneDesc.cpuDispatcher = dispatcher
        sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
        scene = physics.createScene(sceneDesc)

        //cleanup
        sceneDesc.destroy();
        tolerances.destroy()
    }

//    {
//        // create a large static box with size 20x1x20 as ground
//        PxBoxGeometry groundGeometry = new PxBoxGeometry(10f, 0.5f, 10f);   // PxBoxGeometry uses half-sizes
//        PxShape groundShape = physics . createShape (groundGeometry, material, true, shapeFlags);
//        PxRigidStatic ground = physics . createRigidStatic (tmpPose);
//        groundShape.setSimulationFilterData(tmpFilterData);
//        ground.attachShape(groundShape);
//        scene.addActor(ground);
//
//        // create a small dynamic box with size 1x1x1, which will fall on the ground
//        tmpVec.setX(0f); tmpVec.setY(5f); tmpVec.setZ(0f);
//        tmpPose.setP(tmpVec);
//        PxBoxGeometry boxGeometry = new PxBoxGeometry(0.5f, 0.5f, 0.5f);   // PxBoxGeometry uses half-sizes
//        PxShape boxShape = physics . createShape (boxGeometry, material, true, shapeFlags);
//        PxRigidDynamic box = physics . createRigidDynamic (tmpPose);
//        boxShape.setSimulationFilterData(tmpFilterData);
//        box.attachShape(boxShape);
//        scene.addActor(box);
//
//        // clean up temp objects
//        groundGeometry.destroy();
//        boxGeometry.destroy();
//        tmpFilterData.destroy();
//        tmpPose.destroy();
//        tmpVec.destroy();
//        shapeFlags.destroy();
//    }


    fun createActor(id: UUID,  pose: Transform,  shape: PxShape): PxRigidDynamic {
        val body = physics.createRigidDynamic(pose.toPx())
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actors[id] = ActorData(body, shape, physics.createMaterial(.5f, .5f, .5f)) //TODO: remove hotfic material
        LOGGER.info(body.mass.toString())
        return body
    }

    fun createShape(geometry: PxGeometry): PxShape {
        return physics.createShape(geometry, physics.createMaterial(.5f, .5f, .5f))
    }

    fun createBoxActor(id: UUID,  pose: Transform,  aabb: Box): PxRigidDynamic {
        val material = physics.createMaterial(.5f, .5f, .5f)
        val shape = physics.createShape(PxBoxGeometry(aabb.lengthX.toFloat(), aabb.lengthY.toFloat(), aabb.lengthZ.toFloat()), material, true, shapeFlags)
        val body = physics.createRigidDynamic(pose.toPx())
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actors[id] = ActorData(body, shape, material)
        return body
    }

    fun release() {
        scene.release()
    }

    fun tick(world: World) {
        for(player in world.players) {
            val data =  terrainData.computeIfAbsent(player) { a ->
                val actor = physics.createRigidStatic(Transform(player.blockPos.withY(-61).toJOMLD()).toPx())
                val shape = physics.createShape(PxBoxGeometry(100f, 1f, 100f),  physics.createMaterial(.5f, .5f, .5f))
                shape.simulationFilterData = filterData
                actor.attachShape(shape)
                scene.addActor(actor)
                TerrainData(TerrainGeometry(world, world.chunkManager), shape,  actor)
            }

            val actor = data.actor

//            val callback = TerrainGeometry(world, world.chunkManager)
//            val shape = PxBoxGeometry(100f, 1f, 100f)
//
//            actor.detachShape(data.shape)
//            data.shape = shape
//            actor.attachShape(shape)

            actor.setGlobalPose(Transform(player.blockPos.withY(-61).toJOMLD()).toPx(), true)
        }

    }

    fun fetch(id: UUID): Transform {
        val actor = actors.computeIfAbsent(id) { a ->
            LOGGER.info("shit broke man :)")
            throw NullPointerException()
        }

        return actor.actor.globalPose.toCrystal()
    }

    class ContraptionActor {
        //TODO: use to make createshape good
    }
}