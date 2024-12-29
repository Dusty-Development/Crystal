package net.dustley.mixin.accessor;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkLoadingManager.class)
public interface ServerChunkLoadingManagerAccessor {

    @Invoker("entryIterator")
    Iterable<ChunkHolder> callEntryIterator();

    @Invoker("getChunkHolder")
    ChunkHolder callGetChunkHolder(long l);

    @Invoker("save")
    boolean callSave(Chunk chunk);

    @Accessor("unloadedChunks")
    LongSet getUnloadedChunks();

//    // Cannot find this be weary
//    @Accessor("ticketManager")
//    ServerChunkLoadingManager getTicketManager();
}