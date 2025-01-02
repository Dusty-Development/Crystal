package net.dustley.mixin.server.world;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.dustley.accessor.ContraptionManagerAccessor;
import net.dustley.crystal.contraption.Contraption;
import net.dustley.crystal.contraption.server.ServerContraptionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ContraptionManagerAccessor {
    @Shadow @Final private ServerChunkManager chunkManager;
    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract void playSound(@Nullable PlayerEntity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed);

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
        final ServerChunkLoadingManager chunkLoadingManager = self.getChunkManager().chunkLoadingManager;

        contraptionManager.tick();

        for (Contraption contraption : contraptionManager.getContraptions().values()) {
            for (ChunkPos chunkPosition : contraption.getPlot().getControlledChunkPositions()) {
                self.setChunkForced(chunkPosition.x, chunkPosition.z, true);
            }
        }

    }

}

