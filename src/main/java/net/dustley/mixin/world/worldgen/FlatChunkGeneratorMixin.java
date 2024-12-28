package net.dustley.mixin.world.worldgen;

import net.dustley.crystal.scrapyard.ScrapyardPlotManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * <p> (thanks to VS2 for creating the original)
 * @author Dustley
 */
@Mixin(FlatChunkGenerator.class)
public class FlatChunkGeneratorMixin {
    @Inject(method = "populateNoise", at = @At("HEAD"), cancellable = true)
    private void preFillFromNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, CallbackInfoReturnable<CompletableFuture<Chunk>> cir) {
        final ChunkPos chunkPos = chunk.getPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }
}
