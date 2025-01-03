package net.dustley.crystal.items

import net.dustley.crystal.api.contraption.contraptionManager
import net.dustley.crystal.api.math.Transform
import net.dustley.crystal.api.math.toMinecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import org.joml.Quaterniond
import org.joml.Vector3d

class ContraptionCreator : Item(Settings().maxCount(1)) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {

        val world = context.world
        val transform = Transform(Vector3d(context.hitPos.x, context.hitPos.y + 0.5, context.hitPos.z), 1.0, Quaterniond())

        if(world is ServerWorld) {
            context.player?.sendMessage(Text.literal("Creating ship!"))

            val contraption = world.contraptionManager().createAndAddContraption(transform)

            val pos = contraption.plot.centerPos.toMinecraft()
            world.setBlockState(BlockPos.ofFloored(pos).withY(0).subtract(Vec3i(1,1,1)), context.world.getBlockState(context.blockPos))
        }

        return super.useOnBlock(context)
    }

}