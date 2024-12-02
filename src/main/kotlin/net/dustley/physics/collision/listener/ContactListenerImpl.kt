package net.dustley.physics.collision.listener

import com.github.stephengold.joltjni.CustomContactListener
import com.github.stephengold.joltjni.Jolt.endl
import com.github.stephengold.joltjni.enumerate.ValidateResult

class ContactListenerImpl : CustomContactListener() {
    override fun onContactAdded(p0: Long, p1: Long, p2: Long, p3: Long) {
        println("Contact added callback $endl")
    }

    override fun onContactPersisted(p0: Long, p1: Long, p2: Long, p3: Long) {
        println("Contact persisted callback $endl")
    }

    override fun onContactRemoved(p0: Long) {
        println("Contact removed callback $endl")
    }

    override fun onContactValidate(p0: Long, p1: Long, p2: Double, p3: Double, p4: Double, p5: Long): Int {
        println("Contact validate callback $endl")
        return ValidateResult.AcceptAllContactsForThisBodyPair.ordinal
    }
}