package net.dustley.mixin.client.world;

import net.dustley.accessor.ContraptionManagerAccessor;
import net.dustley.crystal.contraption.client.ClientContraptionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ContraptionManagerAccessor {
    @Shadow @Final private ClientChunkManager chunkManager;
    @Unique private ClientContraptionManager contraptionManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey registryRef, RegistryEntry dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        // Create a new ContraptionManager for this world
        this.contraptionManager = new ClientContraptionManager((ClientWorld) (Object) this);
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        // Unload the manager when the world is unloaded
        if (contraptionManager != null) {
            contraptionManager.unload();
            contraptionManager = null;
        }
    }

    public ClientContraptionManager crystal$getContraptionManager() {
        return contraptionManager;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(final BooleanSupplier shouldKeepTicking, final CallbackInfo ci) {
        final ClientWorld self = ClientWorld.class.cast(this);
        final ClientContraptionManager contraptionManager = crystal$getContraptionManager();

        contraptionManager.tick();

//        for (Contraption contraption : contraptionManager.getContraptions().values()) {
//            for (ChunkPos chunkPosition : contraption.getPlot().getControlledChunkPositions()) {
//                self.getChunkManager().setChunkForced(chunkPosition, true);
//            }
//        }
    }
}
