package net.dustley.physics.collision.listener

import com.github.stephengold.joltjni.CustomBodyActivationListener
import com.github.stephengold.joltjni.Jolt.endl

class BodyActivationListenerImpl : CustomBodyActivationListener() {
    override fun onBodyActivated(p0: Long, p1: Long) {
        println("A body activated $endl")
    }

    override fun onBodyDeactivated(p0: Long, p1: Long) {
        println("A body de-activated $endl")
    }
}