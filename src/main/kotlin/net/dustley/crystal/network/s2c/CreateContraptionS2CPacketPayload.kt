package net.dustley.crystal.network.s2c

import net.dustley.crystal.Crystal
import net.dustley.crystal.api.contraption.contraptionManager
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.api.math.toJOML
import net.dustley.crystal.api.math.toMinecraft
import net.dustley.crystal.scrapyard.ScrapyardPlot
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.text.Text
import org.joml.Quaterniond
import org.joml.Quaternionf
import org.joml.Vector2i
import java.util.*


class CreateContraptionS2CPacketPayload(val uuid: UUID, val transform: Transform, val plotPos: Vector2i) : CustomPayload {

    constructor(buf: RegistryByteBuf) : this(buf.readUuid(), Transform(buf.readVec3d().toJOML(), buf.readDouble(), Quaterniond(buf.readQuaternionf())), Vector2i(buf.readInt(), buf.readInt()))

    fun write(buf: RegistryByteBuf) {
        buf.writeUuid(uuid)
        buf.writeVec3d(transform.position.toMinecraft())
        buf.writeDouble(transform.scale)
        buf.writeQuaternionf(Quaternionf(transform.rotation))
        buf.writeInt(plotPos.x)
        buf.writeInt(plotPos.y)
    }

    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return CREATE_CONTRAPTION_PACKET_ID
    }

    companion object {
        val CREATE_CONTRAPTION_PACKET_ID: CustomPayload.Id<CreateContraptionS2CPacketPayload> = CustomPayload.Id(Crystal.identifier("create_multiblock_contraption_packet"))
        val CODEC: PacketCodec<in RegistryByteBuf, CreateContraptionS2CPacketPayload> = PacketCodec.of(CreateContraptionS2CPacketPayload::write, ::CreateContraptionS2CPacketPayload)

        fun receive(payload: CreateContraptionS2CPacketPayload, context: ClientPlayNetworking.Context) {
            val uuid = payload.uuid
            val transform = payload.transform
            val plotPos = payload.plotPos
            val world = context.client().world

            context.client().execute {
                // CODE FOR ON CLIENT ONLY GOES HERE
                val contraptionManager = world?.contraptionManager()!!
                contraptionManager.createAndAddContraptionFromPacket(uuid, transform, plotPos)
                val pos = contraptionManager.scrapyard.getPlot(plotPos) ?.let { ScrapyardPlot.plotToBlockPos(Vector2i(it.plotPosition)) }
                context.player().sendMessage(Text.of("${pos?.x}, ${pos?.z}"))
                MinecraftClient.getInstance().keyboard.clipboard = "/tp ${pos?.x} ${pos?.y} ${pos?.z}"
            }
        }
    }
}