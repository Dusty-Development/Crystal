package net.dustley.crystal.network

import net.dustley.crystal.network.s2c.CreateContraptionS2CPacketPayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry


object ModNetworking {

    fun registerCommon() {
        PayloadTypeRegistry.playS2C().register(CreateContraptionS2CPacketPayload.CREATE_CONTRAPTION_PACKET_ID, CreateContraptionS2CPacketPayload.CODEC)
    }

    fun registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(CreateContraptionS2CPacketPayload.CREATE_CONTRAPTION_PACKET_ID, CreateContraptionS2CPacketPayload::receive)
    }
}