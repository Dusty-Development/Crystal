package net.dustley.mixin.server.network;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

/**
 * <p> (thanks to "Valkyrien Skies 2" for creating the original code)
 */
@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
//
//    @WrapOperation(
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"
//            ),
//            method = "onPlayerInteractBlock"
//    )
//    private double skipDistanceCheck(final Vec3 instance, final Vec3 chunkPos, final Operation<Double> getChessboardDistance) {
//        return 0;
//    }
}
