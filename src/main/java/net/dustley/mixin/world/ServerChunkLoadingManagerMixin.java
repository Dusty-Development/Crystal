package net.dustley.mixin.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ServerChunkLoadingManager.class)
public abstract class ServerChunkLoadingManagerMixin {

    @Shadow @Final
    ServerWorld world;
    @Shadow protected abstract NbtCompound updateChunkNbt(NbtCompound nbt);

    /**
     * Force the game send chunk update packets to players watching ship chunks.
     *
     * <p> (thanks to Tri0de for creating the original)
     * @author Dustley
     */
    @Inject(method = "getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;Z)Ljava/util/List;", at = @At("TAIL"), cancellable = true)
    private void postGetPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge, CallbackInfoReturnable<List<ServerPlayerEntity>> cir) {


        final Iterator<ServerPlayerEntity> playersWatchingShipChunk = world.getPlayers().iterator();
        if (!playersWatchingShipChunk.hasNext()) {
            // No players watching this ship chunk, so we don't need to modify anything
            return;
        }

        final List<ServerPlayerEntity> oldReturnValue = cir.getReturnValue();
        final Set<ServerPlayerEntity> watchingPlayers = new HashSet<>(oldReturnValue);

        playersWatchingShipChunk.forEachRemaining(
                player -> {
                    if (player != null) {
                        watchingPlayers.add(player);
                    }
                }
        );

        cir.setReturnValue(new ArrayList<>(watchingPlayers));
    }


}
