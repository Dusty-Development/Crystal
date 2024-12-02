package net.dustley.physics.collision.layer

import com.github.stephengold.joltjni.ObjVsBpFilter

// Class that determines if an object layer can collide with a broadphase layer
object ObjectVsBroadPhaseLayerFilterImpl : ObjVsBpFilter(PhysicsLayers.LAYER_COUNT, BPLayers.LAYER_COUNT) {

    init {
        disablePair(PhysicsLayers.CONTRAPTION, BPLayers.CONTRAPTION);
    }

}