package net.dustley.crystal

import net.dustley.crystal.events.ModEvents
import net.dustley.crystal.network.ModNetworking
import net.fabricmc.api.ClientModInitializer

object CrystalClient : ClientModInitializer {

    override fun onInitializeClient() {
        Crystal.LOGGER.info("Hello Fabric client world!")

        ModEvents.registerClientModEvents()
        ModNetworking.registerClient()
    }

}