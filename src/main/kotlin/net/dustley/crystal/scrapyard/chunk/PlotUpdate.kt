package net.dustley.crystal.scrapyard.chunk

import net.minecraft.world.chunk.ChunkSection
import org.joml.Vector3ic

data class PlotUpdate(
    val chunkSectionPos: Vector3ic,
    val chunkSectionID : Int,
    val chunkSection: ChunkSection
)
