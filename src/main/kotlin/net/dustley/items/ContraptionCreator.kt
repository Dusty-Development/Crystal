package net.dustley.items

import net.dustley.api.contraption.contraptionManager
import net.dustley.api.math.Transform
import net.dustley.api.math.toJOML
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import org.joml.Quaterniond
import org.joml.Vector3d

class ContraptionCreator : Item(Settings().maxCount(1)) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        context.player?.sendMessage(Text.literal("Creating ship!"))

        val world = context.world
        val transform = Transform(Vector3d(context.hitPos.x, context.hitPos.y, context.hitPos.z), Vector3d(1.0,1.0,1.0), Quaterniond())
        if(world is ServerWorld) {
            val contraption = world.contraptionManager().createContraption(transform)
            val piece = contraption.addPiece()
            println(piece.plot.centerChunk.getBlockPos(8,100,8).toJOML())
        }

        return super.useOnBlock(context)
    }

}