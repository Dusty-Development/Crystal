package net.dustley.mixin.server;

import net.dustley.crystal.contraption.server.ServerContraptionManager;
import net.dustley.accessor.ContraptionManagerAccessor;
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

    public ServerContraptionManager getContraptionManager() { return contraptionManager; }
}
