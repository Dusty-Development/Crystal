package net.dustley.crystal.contraption.physics

import net.dustley.crystal.Crystal.LOGGER
import net.dustley.crystal.Crystal.foundation
import net.dustley.crystal.Crystal.version
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.api.math.toCrystal
import net.dustley.crystal.api.math.toPx
import net.minecraft.util.math.Box
import net.minecraft.world.World
import org.joml.Vector3d
import physx.PxTopLevelFunctions
import physx.common.*
import physx.geometry.PxBoxGeometry
import physx.geometry.PxGeometry
import physx.physics.*
import java.util.*


class PhysXHandler(threads: Int = 4, world: World) {
    private var dispatcher: PxDefaultCpuDispatcher
    private var physics: PxPhysics
    var scene: PxScene

    val actors: HashMap<UUID,PxRigidDynamic> = hashMapOf()
    private val terrain: PxRigidStatic

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
        val sceneDesc = PxSceneDesc(tolerances);
        sceneDesc.gravity = PxVec3(0f, -10f, 0f)
        sceneDesc.cpuDispatcher = dispatcher
        sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader();
        scene = physics.createScene(sceneDesc);


        // create default simulation shape flags

        val terrainShape = physics.createShape(PxBoxGeometry(100f, 1f, 100f), physics.createMaterial(.5f, .5f, .5f), true, shapeFlags) //TODO: make this not the most dipshittery thing
        terrainShape.simulationFilterData = filterData
        terrain = physics.createRigidStatic(Transform(Vector3d(0.0, 0.0, 0.0)).toPx())
        terrain.attachShape(terrainShape)
        scene.addActor(terrain)

        //cleanup
        sceneDesc.destroy();
        tolerances.destroy()
    }

    //        // create a large static box with size 20x1x20 as ground
//        PxBoxGeometry groundGeometry = new PxBoxGeometry(10f, 0.5f, 10f);   // PxBoxGeometry uses half-sizes
//        PxShape groundShape = physics.createShape(groundGeometry, material, true, shapeFlags);
//        PxRigidStatic ground = physics.createRigidStatic(tmpPose);
//        groundShape.setSimulationFilterData(tmpFilterData);
//        ground.attachShape(groundShape);
//        scene.addActor(ground);
//
//        // create a small dynamic box with size 1x1x1, which will fall on the ground
//        tmpVec.setX(0f); tmpVec.setY(5f); tmpVec.setZ(0f);
//        tmpPose.setP(tmpVec);
//        PxBoxGeometry boxGeometry = new PxBoxGeometry(0.5f, 0.5f, 0.5f);   // PxBoxGeometry uses half-sizes
//        PxShape boxShape = physics.createShape(boxGeometry, material, true, shapeFlags);
//        PxRigidDynamic box = physics.createRigidDynamic(tmpPose);
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

    fun createActor(id: UUID, pose: Transform, shape: PxShape): PxRigidDynamic {
        val body = physics.createRigidDynamic(pose.toPx())
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actors[id] = body
        LOGGER.info(body.mass.toString())
        return body
    }

    fun createShape(geometry: PxGeometry): PxShape {
        return physics.createShape(geometry, physics.createMaterial(.5f, .5f, .5f))
    }

    fun createBoxActor(id: UUID, pose: Transform, aabb: Box): PxRigidDynamic {
        val shape = physics.createShape(PxBoxGeometry(aabb.lengthX.toFloat(), aabb.lengthY.toFloat(), aabb.lengthZ.toFloat()), physics.createMaterial(.5f, .5f, .5f), true, shapeFlags)
        val body = physics.createRigidDynamic(pose.toPx())
        shape.simulationFilterData = filterData
        body.attachShape(shape)
        scene.addActor(body)
        actors[id] = body
        LOGGER.info(body.mass.toString())
        return body
    }

    fun release() {
        scene.release()
    }

    fun tick(deltaTime: Float) {
        if(actors.values.isNotEmpty()) {
            val actor = actors.values.first()
            actor.addForce(Vector3d(0.1, 0.0, 0.0).toPx())
        }
        scene.simulate(deltaTime)

    }

    fun fetch(id: UUID): Transform {
        val actor = actors.computeIfAbsent(id) { a ->
            LOGGER.info("shit broke man :)")
            throw NullPointerException()
        }

        return actor.globalPose.toCrystal()
    }

    class ContraptionGeometry {
        //TODO: use to make createshape good
    }
}