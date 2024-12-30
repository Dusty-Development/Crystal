package net.dustley.mixin.world.chunk;

import com.mojang.datafixers.util.Pair;
import net.dustley.crystal.scrapyard.ScrapyardPlotManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <p> (thanks to "Valkyrien Skies 2" for creating the original code)
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

    // TODO its pretty standard to extend this class, if they do super.whatever, these mixins will not work correctly
    // tfc in forge part of the mod has a bandaid solution, if this is fixed please remove that
    @Inject(method = "locateStructure*", at = @At("HEAD"), cancellable = true)
    private void preFindNearestMapFeature(ServerWorld ServerWorld, RegistryEntryList<Structure> holderSet, BlockPos blockPos, int i, boolean bl, CallbackInfoReturnable<Pair<BlockPos, RegistryEntry<Structure>>> cir) {
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4))) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "generateFeatures", at = @At("HEAD"), cancellable = true)
    private void preApplyBiomeDecoration(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) {
        final ChunkPos chunkPos = chunk.getPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            ci.cancel();
        }
    }

    @Inject(method = "setStructureStarts", at = @At("HEAD"), cancellable = true)
    private void preCreateStructures(DynamicRegistryManager registryManager, StructurePlacementCalculator placementCalculator, StructureAccessor structureAccessor, Chunk chunk, StructureTemplateManager structureTemplateManager, CallbackInfo ci) {
        final ChunkPos chunkPos = chunk.getPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            ci.cancel();
        }
    }

    @Inject(method = "addStructureReferences", at = @At("HEAD"), cancellable = true)
    private void preCreateReferences(StructureWorldAccess world, StructureAccessor structureAccessor, Chunk chunk, CallbackInfo ci) {
        final ChunkPos chunkPos = chunk.getPos();
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(chunkPos)) {
            ci.cancel();
        }
    }

}