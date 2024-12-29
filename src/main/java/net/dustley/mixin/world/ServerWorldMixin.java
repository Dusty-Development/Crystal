package net.dustley.mixin.world;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.dustley.accessor.ContraptionManagerAccessor;
import net.dustley.crystal.Crystal;
import net.dustley.crystal.contraption.Contraption;
import net.dustley.crystal.contraption.server.ServerContraptionManager;
import net.dustley.crystal.scrapyard.chunk.PlotUpdate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ContraptionManagerAccessor {
    @Shadow @Final private ServerChunkManager chunkManager;
    @Shadow @Final private MinecraftServer server;
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


    // Map from ChunkPos to the list of voxel chunks that chunk owns
    @Unique
    private final Map<ChunkPos, List<Vector3ic>> crystal$knownChunks = new HashMap<>();

    // Maps chunk pos to number of ticks we have considered unloading the chunk
    @Unique
    private final Long2LongOpenHashMap crystal$chunksToUnload = new Long2LongOpenHashMap();

    // How many ticks we wait before unloading a chunk
    @Unique
    private static final long crystal$CHUNK_UNLOAD_WAIT = 100;

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(final BooleanSupplier shouldKeepTicking, final CallbackInfo ci) {
        final ServerWorld self = ServerWorld.class.cast(this);
        final ServerContraptionManager contraptionManager = crystal$getContraptionManager();
//        final ServerChunkLoadingManager chunkLoadingManager = self.getChunkManager().chunkLoadingManager;
//        final ServerChunkLoadingManagerAccessor chunkLoadingManagerAccessor = (ServerChunkLoadingManagerAccessor) chunkLoadingManager;

        contraptionManager.tick();

        // Create DenseVoxelShapeUpdate for new loaded chunks
        // Also mark the chunks as loaded in the ship objects
        final List<PlotUpdate> voxelShapeUpdates = new ArrayList<>();
//        final ChunkTicketManagerAccessor ticketManager = (ChunkTicketManagerAccessor) (chunkLoadingManager.getTicketManager());

        for (Contraption contraption : contraptionManager.getContraptions().values()) {
            Crystal.INSTANCE.getLOGGER().info(String.valueOf(self.isChunkLoaded(contraption.getPlot().getCenterChunkPos().x, contraption.getPlot().getCenterChunkPos().z)));

            for (ChunkPos chunkPosition : contraption.getPlot().getControlledChunkPositions()) {
//                self.getChunkManager().chunkLoadingManager.createLoader(ChunkStatus.FULL, chunkPosition);
//                self.setChunkForced(chunkPosition.x, chunkPosition.z, true);
//                self.getChunkManager().addTicket(ChunkTicketType.PLAYER, chunkPosition, 0, chunkPosition);
            }
//            self.isChunkLoaded(contraption.getPlot().getCenterChunkPos().x, contraption.getPlot().getCenterChunkPos().z);
        }


//
//        for (final ChunkHolder chunkHolder : chunkLoadingManagerAccessor.callEntryIterator()) {
//            final Optional<WorldChunk> worldChunkOptional = chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
//            // Only load chunks that are present and that have tickets
//            if (worldChunkOptional.isPresent() && ticketManager.getTickets().containsKey(chunkHolder.getPos().toLong())) {
//                // Only load chunks that have a ticket
//                final WorldChunk worldChunk = worldChunkOptional.get();
//                crystal$loadChunk(worldChunk, voxelShapeUpdates);
//            }
//        }


        // The commented code below is for unloading...
        // I could give 2 fucks less about that rn tho sooooooo-
        // ignore it

//
//        final Iterator<Map.Entry<ChunkPos, List<Vector3ic>>> knownChunkPosIterator = crystal$knownChunks.entrySet().iterator();
//        while (knownChunkPosIterator.hasNext()) {
//            final Map.Entry<ChunkPos, List<Vector3ic>> knownChunkPosEntry = knownChunkPosIterator.next();
//            final long chunkPos = knownChunkPosEntry.getKey().toLong();
//            // Unload chunks if they don't have tickets or if they're not in the visible chunks
//            if ((!distanceManagerAccessor.getTickets().containsKey(chunkPos) || chunkMapAccessor.callGetVisibleChunkIfPresent(chunkPos) == null)) {
//                final long ticksWaitingToUnload = crystal$chunksToUnload.getOrDefault(chunkPos, 0L);
//                if (ticksWaitingToUnload > crystal$CHUNK_UNLOAD_THRESHOLD) {
//                    // Unload this chunk
//                    for (final Vector3ic unloadedChunk : knownChunkPosEntry.getValue()) {
//                        final TerrainUpdate deleteVoxelShapeUpdate =
//                                getVsCore().newDeleteTerrainUpdate(unloadedChunk.x(), unloadedChunk.y(), unloadedChunk.z());
//                        voxelShapeUpdates.add(deleteVoxelShapeUpdate);
//                    }
//                    knownChunkPosIterator.remove();
//                    crystal$chunksToUnload.remove(chunkPos);
//                } else {
//                    crystal$chunksToUnload.put(chunkPos, ticksWaitingToUnload + 1);
//                }
//            }
//        }

        // Tell the manager to disperse updates
        contraptionManager.applyPlotUpdates(voxelShapeUpdates);
    }


    @Unique
    private void crystal$loadChunk(@NotNull final Chunk worldChunk, final List<PlotUpdate> voxelShapeUpdates) {
        // Remove the chunk pos from crystal$chunksToUnload if its present
        crystal$chunksToUnload.remove(worldChunk.getPos().toLong());
        if (!crystal$knownChunks.containsKey(worldChunk.getPos())) {
            final List<Vector3ic> voxelChunkPositions = new ArrayList<>();

            final int chunkX = worldChunk.getPos().x;
            final int chunkZ = worldChunk.getPos().z;

            final ChunkSection[] chunkSections = worldChunk.getSectionArray();

            for (int sectionY = 0; sectionY < chunkSections.length; sectionY++) {
                final ChunkSection chunkSection = chunkSections[sectionY];
                final Vector3ic chunkPos = new Vector3i(chunkX, worldChunk.sectionIndexToCoord(sectionY), chunkZ);
                voxelChunkPositions.add(chunkPos);

                if (chunkSection != null && !chunkSection.isEmpty()) {
                    // Add this chunk to the ground rigid body
                    final PlotUpdate voxelShapeUpdate = new PlotUpdate(chunkPos, sectionY, chunkSection);
                    voxelShapeUpdates.add(voxelShapeUpdate);
                }
//                else {
//                    final PlotUpdate emptyVoxelShapeUpdate = getVsCore()
//                            .newEmptyVoxelShapeUpdate(chunkPos.x(), chunkPos.y(), chunkPos.z(), true);
//                    voxelShapeUpdates.add(emptyVoxelShapeUpdate);
//                }
            }
            crystal$knownChunks.put(worldChunk.getPos(), voxelChunkPositions);
        }
    }

    @Unique
    public void crystal$removeChunk(final int chunkX, final int chunkZ) {
        final ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        crystal$knownChunks.remove(chunkPos);
    }

}
