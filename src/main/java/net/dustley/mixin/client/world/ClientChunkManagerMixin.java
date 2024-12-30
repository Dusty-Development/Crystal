package net.dustley.mixin.client.world;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import net.dustley.crystal.scrapyard.ScrapyardPlotManager;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

/**
 * The purpose of this mixin is to allow {@link ClientChunkManager} to store ship chunks.
 * <p> (thanks to "Valkyrien Skies 2" for creating the original code)
 */
@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin {

    @Shadow
    @Final
    ClientWorld world;

    @Shadow private volatile ClientChunkManager.ClientChunkMap chunks;

    @Unique
    public LongObjectMap<WorldChunk> crystal$getShipChunks() {
        return crystal$shipChunks;
    }

    @Unique
    private final LongObjectMap<WorldChunk> crystal$shipChunks = new LongObjectHashMap<>();

    @Inject(method = "loadChunkFromPacket", at = @At("HEAD"), cancellable = true)
    private void preLoadChunkFromPacket(int x, int z, PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> cir) {

        if (!chunks.isInRadius(x,z)) {
            if (ScrapyardPlotManager.Companion.isChunkInScrapyard(new ChunkPos(x,z))) {
                final long chunkPosLong = new ChunkPos(x, z).toLong();

                final WorldChunk oldChunk = crystal$shipChunks.get(chunkPosLong);
                final WorldChunk worldChunk;
                if (oldChunk != null) {
                    worldChunk = oldChunk;
                    oldChunk.loadFromPacket(buf, nbt, consumer);
                } else {
                    worldChunk = new WorldChunk(this.world, new ChunkPos(x, z));
                    worldChunk.loadFromPacket(buf, nbt, consumer);
                    crystal$shipChunks.put(chunkPosLong, worldChunk);
                }

                this.world.resetChunkColor(new ChunkPos(x, z));
//                SodiumCompat.onChunkAdded(this.world, x, z);
                cir.setReturnValue(worldChunk);
            }
        }
    }

    @Inject(method = "unload", at = @At("HEAD"), cancellable = true)
    public void preUnload(ChunkPos pos, CallbackInfo ci) {
        if (ScrapyardPlotManager.Companion.isChunkInScrapyard(pos)) {
            crystal$shipChunks.remove(pos.toLong());
//            if (true) { // ValkyrienCommonMixinConfigPlugin.getCrystalRenderer() != CrystalRenderer.SODIUM
//                ((ICrystalViewAreaMethods) ((WorldRendererAccessor) ((ClientWorldAccessor) world).getWorldRenderer()).getViewArea())
//                    .unloadChunk(chunkX, chunkZ);
//            }
//            SodiumCompat.onChunkRemoved(this.world, chunkX, chunkZ);
            ci.cancel();
        }
    }

    @Inject(
        method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/WorldChunk;",
        at = @At("HEAD"), cancellable = true)
    public void preGetChunk(final int chunkX, final int chunkZ, final ChunkStatus chunkStatus, final boolean bl, final CallbackInfoReturnable<WorldChunk> cir) {
        final WorldChunk shipChunk = crystal$shipChunks.get(new ChunkPos(chunkX, chunkZ).toLong());
        if (shipChunk != null) {
            cir.setReturnValue(shipChunk);
        }
    }
}
