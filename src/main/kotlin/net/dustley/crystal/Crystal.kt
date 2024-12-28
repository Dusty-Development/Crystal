package net.dustley.crystal

import net.dustley.crystal.events.ModEvents
import net.dustley.crystal.items.ModItems
import net.dustley.crystal.network.ModNetworking
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import physx.PxTopLevelFunctions
import physx.common.PxDefaultAllocator
import physx.common.PxDefaultErrorCallback
import physx.common.PxFoundation


object Crystal : ModInitializer {
	const val MOD_ID = "crystal"
	const val IS_DEBUG = true
    val LOGGER: Logger = LoggerFactory.getLogger("Crystal")
	val version: Int = PxTopLevelFunctions.getPHYSICS_VERSION()
	var allocator: PxDefaultAllocator = PxDefaultAllocator()
	var errorCb: PxDefaultErrorCallback = PxDefaultErrorCallback()
	var foundation: PxFoundation? = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCb)

	fun identifier(id: String):Identifier = Identifier.of(MOD_ID, id)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		val versionMajor: Int = version shr 24
		val versionMinor: Int = (version shr 16) and 0xff
		val versionMicro: Int = (version shr 8) and 0xff
		LOGGER.info("PhysX loaded, version: ${versionMajor}.${versionMinor}.${versionMicro}")

		if(foundation == null) {
			LOGGER.info("THIS SHIT NULL")
		}

		println("Test")
		ModEvents.registerModEvents()
		ModNetworking.registerCommon()

		ModItems.registerModItems()
	}

}