package net.dustley.mixin.client;

import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "render", at = @At("TAIL"))
    public void renderCustomPlot(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f matrix4f,
            Matrix4f matrix4f2,
            CallbackInfo ci
    ) {
//        // Call your plot rendering logic
//        var renderer = new ContraptionRenderSystem(MinecraftClient.getInstance().world, camera);
//        renderer.updateAndRender(0.05, renderer.);
    }

}