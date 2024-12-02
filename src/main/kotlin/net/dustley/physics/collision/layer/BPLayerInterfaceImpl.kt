package net.dustley.physics.collision.layer

import com.github.stephengold.joltjni.MapObj2Bp

// BroadPhaseLayerInterface implementation
// This defines a mapping between object and broadphase layers.
object BPLayerInterfaceImpl : MapObj2Bp(PhysicsLayers.LAYER_COUNT, BPLayers.LAYER_COUNT) {

    init {
        add(PhysicsLayers.WORLD, BPLayers.WORLD)
    }

}