package net.dustley.crystal.contraption.physics

import net.dustley.crystal.Crystal
import net.dustley.crystal.Crystal.LOGGER
import net.dustley.crystal.Crystal.version
import net.dustley.crystal.Crystal.foundation
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.api.math.toCrystal
import net.minecraft.world.World
import org.jetbrains.annotations.NotNull
import physx.PxTopLevelFunctions
import physx.common.*
import physx.geometry.PxBoxGeometry
import physx.geometry.PxGeometry
import physx.geometry.PxPlaneGeometry
import physx.physics.*
import java.util.*


class PhysXHandler(threads: Int = 4, world: World) {
    private var dispatcher: PxDefaultCpuDispatcher
    private var physics: PxPhysics
    private var scene: PxScene

    private val actors: HashMap<UUID,PxRigidDynamic> = hashMapOf()
    private val terrain: PxRigidStatic

    private val tmpFilterData = PxFilterData(1, 1, 0, 0)

    init {
        if(foundation == null) {
            LOGGER.info("null")
        }
        val tolerances = PxTolerancesScale()
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances);

        dispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(threads)

        // create a physics scene
        val sceneDesc = PxSceneDesc(tolerances);
        sceneDesc.gravity = PxVec3(0f, -1f, 0f)
        sceneDesc.cpuDispatcher = dispatcher
        //sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader()); literally no idea what this does
        scene = physics.createScene(sceneDesc);

        val terrainShape = physics.createShape(PxPlaneGeometry(), physics.createMaterial(.5f, .5f, .5f)) //TODO: make this not the most dipshittery thing
        terrainShape.setSimulationFilterData(tmpFilterData);
        terrain = physics.createRigidStatic(PxTransform(PxIDENTITYEnum.PxIdentity))
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
        body.attachShape(shape)
        scene.addActor(body)
        actors[id] = body
        return body
    }

    fun createShape(geometry: PxGeometry): PxShape {
        //TODO: make this good
        return physics.createShape(PxBoxGeometry(1.0f, 1.0f, 1.0f), physics.createMaterial(.5f, .5f, .5f))
    }

    fun createBox(
        //a: Vector3f, b: Vector3f
    ): PxShape { //TODO: find AABB
        return physics.createShape(PxBoxGeometry(1.0f, 1.0f, 1.0f), physics.createMaterial(.5f, .5f, .5f))
    }

    fun release() {
        scene.release()
    }

    fun tick(deltaTime: Float) {
        scene.simulate(deltaTime);
    }

    fun fetch(id: UUID): Transform {
        scene.fetchResults(true);

        val actor = actors.computeIfAbsent(id) { a ->
            Crystal.LOGGER.info("shit broke man :)")
            throw NullPointerException()
        }

        return actor.globalPose.toCrystal()
    }

    class ContraptionGeometry {
        //TODO: use to make createshape good
    }
}