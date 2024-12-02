package net.dustley.physics.collision.layer

import com.github.stephengold.joltjni.ObjVsObjFilter

// Class that determines if two object layers can collide
object ObjectLayerPairFilterImpl : ObjVsObjFilter(PhysicsLayers.LAYER_COUNT) {
    init {
        disablePair(PhysicsLayers.WORLD, PhysicsLayers.WORLD)
    }
}