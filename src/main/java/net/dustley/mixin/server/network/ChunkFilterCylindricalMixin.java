package net.dustley.mixin.server.network;

import net.dustley.crystal.scrapyard.ScrapyardPlotManager;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkFilter.Cylindrical.class)
public class ChunkFilterCylindricalMixin {

    @Inject(method = "isWithinDistance", at = @At("HEAD"), cancellable = true)
    private void distanceCheck(int x, int z, boolean includeEdge, CallbackInfoReturnable<Boolean> cir) {
        ChunkPos centerPos = new ChunkPos(x, z);

        // TODO: might be better as a check if the origin is in the scrapyard, if so move it back and check for other chunks too
        // Just in case players or loaders are on ships
        if(ScrapyardPlotManager.Companion.isChunkInScrapyard(centerPos)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
