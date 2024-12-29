package net.dustley.mixin.world;

import net.dustley.accessor.ContraptionManagerAccessor;
import net.dustley.mixin.accessor.ServerChunkLoadingManagerAccessor;
import net.dustley.crystal.contraption.server.ServerContraptionManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ContraptionManagerAccessor {
    @Unique private ServerContraptionManager contraptionManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        this.contraptionManager = new ServerContraptionManager((ServerWorld) (Object) this);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        // Unload the manager when the world is unloaded
        if (contraptionManager != null) {
            contraptionManager.unload();
            contraptionManager = null;
        }
    }

    public ServerContraptionManager crystal$getContraptionManager() { return contraptionManager; }

    @Inject(method = "tick", at = @At("TAIL"))
    private void postTick(final BooleanSupplier shouldKeepTicking, final CallbackInfo ci) {
        final ServerWorld self = ServerWorld.class.cast(this);
        final ServerContraptionManager contraptionManager = crystal$getContraptionManager();
        final ServerChunkLoadingManagerAccessor chunkLoadingManager = (ServerChunkLoadingManagerAccessor) self.getChunkManager().chunkLoadingManager;

        contraptionManager.tick();
        
//        final ChunkTicketManagerAccessor ticketManager = (ChunkTicketManagerAccessor) (chunkLoadingManager.getTicketManager());
        // Create DenseVoxelShapeUpdate for new loaded chunks
        // Also mark the chunks as loaded in the ship objects
//        final List<TerrainUpdate> voxelShapeUpdates = new ArrayList<>();
//        final DistanceManagerAccessor distanceManagerAccessor = (DistanceManagerAccessor) chunkSource.chunkMap.getDistanceManager();
//
//        for (final ChunkHolder chunkHolder : chunkLoadingManager.callEntryIterator()) {
//            final Optional<WorldChunk> worldChunkOptional = chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
//            // Only load chunks that are present and that have tickets
//            if (worldChunkOptional.isPresent() && ticketManager.getTickets().containsKey(chunkHolder.getPos().toLong())) {
//                // Only load chunks that have a ticket
//                final WorldChunk worldChunk = worldChunkOptional.get();
////                vs$loadChunk(worldChunk, voxelShapeUpdates);
//            }
//        }
//
//        final Iterator<Map.Entry<ChunkPos, List<Vector3ic>>> knownChunkPosIterator = vs$knownChunks.entrySet().iterator();
//        while (knownChunkPosIterator.hasNext()) {
//            final Map.Entry<ChunkPos, List<Vector3ic>> knownChunkPosEntry = knownChunkPosIterator.next();
//            final long chunkPos = knownChunkPosEntry.getKey().toLong();
//            // Unload chunks if they don't have tickets or if they're not in the visible chunks
//            if ((!distanceManagerAccessor.getTickets().containsKey(chunkPos) || chunkMapAccessor.callGetVisibleChunkIfPresent(chunkPos) == null)) {
//                final long ticksWaitingToUnload = vs$chunksToUnload.getOrDefault(chunkPos, 0L);
//                if (ticksWaitingToUnload > VS$CHUNK_UNLOAD_THRESHOLD) {
//                    // Unload this chunk
//                    for (final Vector3ic unloadedChunk : knownChunkPosEntry.getValue()) {
//                        final TerrainUpdate deleteVoxelShapeUpdate =
//                                getVsCore().newDeleteTerrainUpdate(unloadedChunk.x(), unloadedChunk.y(), unloadedChunk.z());
//                        voxelShapeUpdates.add(deleteVoxelShapeUpdate);
//                    }
//                    knownChunkPosIterator.remove();
//                    vs$chunksToUnload.remove(chunkPos);
//                } else {
//                    vs$chunksToUnload.put(chunkPos, ticksWaitingToUnload + 1);
//                }
//            }
//        }
//
//        // Send new loaded chunks updates to the ship world
//        shipObjectWorld.addTerrainUpdates(
//                VSGameUtilsKt.getDimensionId(self),
//                voxelShapeUpdates
//        );
    }

}
