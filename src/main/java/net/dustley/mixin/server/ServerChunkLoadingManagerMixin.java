package net.dustley.mixin.server;

import net.dustley.api.contraption.ContraptionManagerAccessorKt;
import net.dustley.contraption.ContraptionManager;
import net.dustley.contraption.piece.ContraptionPiece;
import net.dustley.scrapyard.ScrapyardPlot;
import net.dustley.scrapyard.ScrapyardPlotManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkLoadingManager.class)
public abstract class ServerChunkLoadingManagerMixin {


    @Shadow @Final
    ServerWorld world;
    @Shadow protected abstract NbtCompound updateChunkNbt(NbtCompound nbt);

    /**
     * Force the game to generate empty chunks in the scrapyard.
     *
     * <p>If a chunk already exists do nothing. If it doesn't yet exist, but it's in the scrapyard, then pretend that
     * chunk already existed and return a new chunk.
     *
     * <p> (thanks to Tri0de for creating the original)
     * @author Dustley
     */
    @Inject(method = "getUpdatedChunkNbt", at = @At("HEAD"), cancellable = true)
    private void preReadChunk(final ChunkPos chunkPos, final CallbackInfoReturnable<CompletableFuture<Optional<NbtCompound>>> cir) {
        final ServerChunkLoadingManager self = ServerChunkLoadingManager.class.cast(this);

        cir.setReturnValue(self.getNbt(chunkPos).thenApplyAsync(nbtCompound -> {
            if (nbtCompound.isEmpty()) {
                final ContraptionManager contraptionManager = ContraptionManagerAccessorKt.contraptionManager(world);
                final ScrapyardPlotManager scrapyardPlotManager = contraptionManager.getScrapyard();
                final ScrapyardPlot plot = scrapyardPlotManager.getPlot(chunkPos);

                // If its in a ship and it shouldn't generate chunks OR if there is no ship but its happening in the shipyard
                if (plot == null && scrapyardPlotManager.isChunkInScrapyard(chunkPos)) {
                    // The chunk doesn't yet exist and is in the shipyard. Make a new empty chunk
                    // Generate the chunk to be nothing
                    final WorldChunk generatedChunk = new WorldChunk(world, new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA, world, world.getRegistryManager().get(RegistryKeys.BIOME), null), null);

                    // Its wasteful to serialize just for this to be deserialized, but it will work for now.
                    return Optional.of(ChunkSerializer.serialize(world, generatedChunk));
                }
            }
            return nbtCompound.map(this::updateChunkNbt);
        }));

    }

    /**
     * Force the game send chunk update packets to players watching ship chunks.
     *
     * @author Tri0de
     */
    @Inject(method = "getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;Z)Ljava/util/List;", at = @At("TAIL"), cancellable = true)
    private void postGetPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge, CallbackInfoReturnable<List<ServerPlayerEntity>> cir) {
//
//        final Iterator<IPlayer> playersWatchingShipChunk =
//                VSGameUtilsKt.getShipObjectWorld(level)
//                        .getIPlayersWatchingShipChunk(chunkPos.x, chunkPos.z, VSGameUtilsKt.getDimensionId(level));
//
//        if (!playersWatchingShipChunk.hasNext()) {
//            // No players watching this ship chunk, so we don't need to modify anything
//            return;
//        }
//
//        final List<ServerPlayer> oldReturnValue = cir.getReturnValue();
//        final Set<ServerPlayer> watchingPlayers = new HashSet<>(oldReturnValue);
//
//        playersWatchingShipChunk.forEachRemaining(
//                iPlayer -> {
//                    final MinecraftPlayer minecraftPlayer = (MinecraftPlayer) iPlayer;
//                    final ServerPlayer playerEntity =
//                            (ServerPlayer) minecraftPlayer.getPlayerEntityReference().get();
//                    if (playerEntity != null) {
//                        watchingPlayers.add(playerEntity);
//                    }
//                }
//        );
//
//        cir.setReturnValue(new ArrayList<>(watchingPlayers));
    }

}
