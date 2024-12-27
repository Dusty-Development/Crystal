package net.dustley.crystal

import net.dustley.crystal.events.ModEvents
import net.dustley.crystal.items.ModItems
import net.dustley.crystal.network.ModNetworking
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Crystal : ModInitializer {
	const val MOD_ID = "crystal"
	const val IS_DEBUG = true
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

	fun identifier(id: String):Identifier = Identifier.of(MOD_ID, id)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello Fabric world!")

		println("Test")
		ModEvents.registerModEvents()
		ModNetworking.registerCommon()

		ModItems.registerModItems()
	}

}