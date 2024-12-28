package net.dustley.crystal.api.math

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import java.math.BigInteger

data class BitMap(var map: BigInteger) { //TODO: finish this

    companion object {
        fun createFromChunk(world: ServerWorld, pos: ChunkPos): BitMap {
            return createEmpty()
        }

        fun createEmpty(): BitMap {
            return BitMap(BigInteger(ByteArray(12256)))
        }
    }
}
