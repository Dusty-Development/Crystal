package net.dustley.mixin.client;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Nullable private ClientWorld world;

    @ModifyConstant(
            method = "render",
            constant = @Constant(
                    doubleValue = 1024,
                    ordinal = 0
            ))
    private double disableBlockDamageDistanceCheck(final double originalBlockDamageDistanceConstant) {
        return Double.MAX_VALUE;
    }


}