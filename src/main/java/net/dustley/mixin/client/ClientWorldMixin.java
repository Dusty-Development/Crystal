package net.dustley.mixin.client;

import net.dustley.mixin.accessor.ContraptionManagerAccessor;
import net.dustley.crystal.contraption.client.ClientContraptionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ContraptionManagerAccessor {
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
}
