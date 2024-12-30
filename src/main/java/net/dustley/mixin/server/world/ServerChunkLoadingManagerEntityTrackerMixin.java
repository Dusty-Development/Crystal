package net.dustley.mixin.server.world;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.dustley.crystal.api.contraption.ContraptionManagerAccessorKt;
import net.dustley.crystal.api.math.VectorConversionsMCKt;
import net.dustley.crystal.contraption.Contraption;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * <p> (thanks to "Valkyrien Skies 2" for creating the original code)
 */
@Mixin(ServerChunkLoadingManager.EntityTracker.class)
public class ServerChunkLoadingManagerEntityTrackerMixin {

    @Shadow
    @Final
    Entity entity;

    @Unique
    private Contraption inCallContraption = null;

    @ModifyExpressionValue(method = "updateTrackedStatus(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPos()Lnet/minecraft/util/math/Vec3d;"))
    Vec3d includeShips(final Vec3d original) {
        final BlockPos pos = BlockPos.ofFloored(original);
        final Contraption contraption = inCallContraption = ContraptionManagerAccessorKt.contraptionManager(entity.getWorld()).getContraptionManagingPos(pos);
        if (contraption != null) {
            return VectorConversionsMCKt.toMinecraft(contraption.getTransform().getPosition());
        } else {
            return original;
        }
    }

    @WrapOperation(method = "updateTrackedStatus(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canBeSpectated(Lnet/minecraft/server/network/ServerPlayerEntity;)Z"))
    boolean skipWeirdCheck(final Entity instance, final ServerPlayerEntity serverPlayer, final Operation<Boolean> canBeSpectated) {
        return inCallContraption != null || canBeSpectated.call(instance, serverPlayer);
    }


}
