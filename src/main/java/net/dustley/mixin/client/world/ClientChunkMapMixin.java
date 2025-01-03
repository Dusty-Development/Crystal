package net.dustley.mixin.client.world;

import net.dustley.crystal.scrapyard.ScrapyardPlotManager;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientChunkManager.ClientChunkMap.class)
public class ClientChunkMapMixin {

    @Inject(method = "isInRadius", at = @At("HEAD"), cancellable = true)
    private void radiusCheck(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if(ScrapyardPlotManager.Companion.isChunkInScrapyard(new ChunkPos(chunkX, chunkZ))) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

}
