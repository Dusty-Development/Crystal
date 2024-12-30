package net.dustley.mixin.world.worldgen;

import net.dustley.crystal.scrapyard.ScrapyardPlotManager;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * <p> (thanks to "Valkyrien Skies 2" for creating the original code)
 */
@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Shadow
    @Final
    protected RegistryEntry<ChunkGeneratorSettings> settings;

    @Inject(method = "getColumnSample", at = @At("HEAD"), cancellable = true)
    private void preGetColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig, CallbackInfoReturnable<VerticalBlockSample> cir) {
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(new ChunkPos(x,z))) {

            final GenerationShapeConfig noiseSettings = this.settings.value().generationShapeConfig();
            final int minY = Math.max(noiseSettings.minimumY(), world.getBottomY());
            cir.setReturnValue(new VerticalBlockSample(minY, new BlockState[0]));

        }
    }

    @Inject(method = "buildSurface(Lnet/minecraft/world/ChunkRegion;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/chunk/Chunk;)V", at = @At("HEAD"), cancellable = true)
    private void preBuildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk, CallbackInfo ci) {
        final ChunkPos chunkPos = chunk.getPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            ci.cancel();
        }
    }

    @Inject(method = "carve", at = @At("HEAD"), cancellable = true)
    private void preApplyCarvers(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep, CallbackInfo ci) {
        final ChunkPos chunkPos = chunk.getPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            ci.cancel();
        }
    }

    @Inject(method = "populateNoise(Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/chunk/Chunk;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private void prePopulateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, CallbackInfoReturnable<CompletableFuture<Chunk>> cir) {
        final ChunkPos chunkPos = chunk.getPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }

    @Inject(method = "populateEntities", at = @At("HEAD"), cancellable = true)
    private void preSpawnMobs(ChunkRegion region, CallbackInfo ci) {
        final ChunkPos chunkPos = region.getCenterPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            ci.cancel();
        }
    }

}
