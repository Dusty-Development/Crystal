package net.dustley.mixin.client.render.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.dustley.crystal.api.contraption.ContraptionManagerAccessorKt;
import net.dustley.crystal.contraption.Contraption;
import net.dustley.crystal.contraption.client.ClientContraption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ListIterator;
import java.util.WeakHashMap;

/**
 * <p> (thanks to "Valkyrien Skies 2" for creating the original code)
 */
@Mixin(value = WorldRenderer.class, priority = 999999)
public class RenderContraptionChunksMixin {
    @Unique
    private final WeakHashMap<ClientContraption, ObjectArrayList<ChunkBuilder.BuiltChunk>> contraptionBuiltChunks = new WeakHashMap<>();
    @Shadow
    private ClientWorld world;

    @Shadow
    @Final
    @Mutable
    private ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks; // was renderChunksInFrustum
    @Shadow
    private @Nullable BuiltChunkStorage chunks;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow private int cameraChunkZ;
    @Unique
    private ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunksGeneratedByVanilla = new ObjectArrayList<>();

    /**
     * Fix the distance to render chunks, so that MC doesn't think contraption chunks are too far away
     */
    @Redirect(
            method = "updateChunks", // formerly compileChunks
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;getSquaredDistance(Lnet/minecraft/util/math/Vec3i;)D"
            ),
            require = 0
    )
    private double includeContraptionChunksInNearChunks(BlockPos pos, Vec3i vec) {
        return ContraptionManagerAccessorKt.contraptionManager(world).getSquaredDistanceWithContraptions(pos.toCenterPos(), Vec3d.of(vec));
    }

//    /**
//     * Force frustum update if the contraption moves and the camera doesn't
//     */
//    @ModifyExpressionValue(
//            method = "setupTerrain", // formerly setupRender
//            at = @At(
//                    value = "INVOKE",
//                    target = "Ljava/util/concurrent/atomic/AtomicBoolean;compareAndSet(ZZ)Z"
//            )
//    )
//    private boolean needsFrustumUpdate(boolean needsFrustumUpdate) {
//        ClientPlayerEntity player = client.player;
//        return needsFrustumUpdate || (player != null && VSGameUtilsKt.getContraptionMountedTo(player) != null);
//    }

    /**
     * Add contraption render chunks to visible chunks
     */
    @Inject(
            method = "setupTerrain",
            at = @At("RETURN")
    )
    private void preSetupRender(Camera camera, Frustum frustum, boolean b1, boolean b2, CallbackInfo ci) {
        addContraptionVisibleChunks(frustum);
    }

    @Unique
    public void addContraptionVisibleChunks(Frustum frustum) {
        builtChunksGeneratedByVanilla = new ObjectArrayList<>(builtChunks);

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        BuiltChunkStorage chunkStorageAccessor = chunks;
        for (Contraption contraptionObject : ContraptionManagerAccessorKt.contraptionManager(world).getContraptions().values()) {
//            if (!frustum.isVisible(VectorConversionsMCKt.toMinecraft(contraptionObject.getRenderAABB()))) {
//                continue;
//            }
            if(!(contraptionObject instanceof ClientContraption)) continue;

            contraptionObject.getPlot().getControlledChunkPositions().forEach((chunkPos) -> {
                Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
                for (int y = world.getBottomSectionCoord(); y < world.getTopSectionCoord(); y++) {
                    mutablePos.set(chunkPos.x << 4, y << 4, chunkPos.z << 4);
                    ChunkBuilder.BuiltChunk builtChunk = chunkStorageAccessor.getRenderedChunk(mutablePos);
                    if (builtChunk != null) {
                        // skip if empty
//                        ChunkSection section = chunk.getSection(y - world.getBottomSectionCoord());
//                        if (section.isEmpty()) {
//                            return;
//                        }
//                        // check frustum
//                        Box box = new Box((x << 4) - 0.6, (y << 4) - 0.6, (z << 4) - 0.6,
//                                (x << 4) + 15.6, (y << 4) + 15.6, (z << 4) + 15.6)
//                                .transform(contraptionObject.getRenderTransform().getContraptionToWorld());
//                        if (!frustum.isVisible(VectorConversionsMCKt.toMinecraft(box))) {
//                            return;
//                        }

                        ChunkBuilder.BuiltChunk newChunkInfo;
                        newChunkInfo = builtChunk;

                        contraptionBuiltChunks.computeIfAbsent((ClientContraption) contraptionObject, k -> new ObjectArrayList<>()).add(newChunkInfo);
                        builtChunks.add(newChunkInfo);
                    }
                }
            });
        }
    }

//    @Inject(
//            method = "*", // wildcard injection
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;clear()V"
//            )
//    )
//    private void clearContraptionChunks(CallbackInfo ci) {
//        contraptionRenderChunks.forEach((contraption, chunks) -> chunks.clear());
//    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"
            )
    )
    private void redirectRenderChunkLayer(WorldRenderer instance, RenderLayer renderLayer, double camX, double camY, double camZ, Matrix4f originalMatrix, Matrix4f positionMatrix, Operation<Void> original) {
        final var originalRenderChunks = builtChunks;
        builtChunks = builtChunksGeneratedByVanilla;

        original.call(instance, renderLayer, camX, camY, camZ, originalMatrix, positionMatrix);
        builtChunks = originalRenderChunks;

        contraptionBuiltChunks.forEach((contraption, chunks) -> {
            Matrix4f matrix = new Matrix4f(originalMatrix);
//            poseStack.pushPose();
            final Vector3dc center = new Vector3d();

            var contraptionTransform = contraption.getTransform();
            var contraptionChunkPosition = contraption.getPlot().getCenterChunkPos();
            matrix.translate(-contraptionChunkPosition.x, 0, -contraptionChunkPosition.z);

            matrix.mul(contraptionTransform.getMatrix4f());

            renderLayer(renderLayer, matrix, center.x(), center.y(), center.z(), matrix, chunks);
        });
    }

    @Unique
    private void renderLayer(RenderLayer layer, Matrix4f matrix,
                                  double x, double y, double z, Matrix4f projection,
                                  ObjectList<ChunkBuilder.BuiltChunk> chunksToRender) {
        RenderSystem.assertOnRenderThread();
        layer.startDrawing();
        client.getProfiler().push("filterempty");
        client.getProfiler().swap("render_" + layer);
        boolean notTranslucent = layer != RenderLayer.getTranslucent();
        ListIterator<?> it = chunksToRender.listIterator(notTranslucent ? 0 : chunksToRender.size());
        ShaderProgram shader = RenderSystem.getShader();

        for (int k = 0; k < 12; ++k) {
            int tex = RenderSystem.getShaderTexture(k);
            shader.addSampler("Sampler" + k, tex);
        }

        if (shader.modelViewMat != null) shader.modelViewMat.set(matrix);
        if (shader.projectionMat != null) shader.projectionMat.set(projection);
        if (shader.colorModulator != null) shader.colorModulator.set(RenderSystem.getShaderColor());
        if (shader.fogStart != null) shader.fogStart.set(RenderSystem.getShaderFogStart());
        if (shader.fogEnd != null) shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        if (shader.fogColor != null) shader.fogColor.set(RenderSystem.getShaderFogColor());
        if (shader.fogShape != null) shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        if (shader.textureMat != null) shader.textureMat.set(RenderSystem.getTextureMatrix());
        if (shader.gameTime != null) shader.gameTime.set(RenderSystem.getShaderGameTime());

        RenderSystem.setupShaderLights(shader);
        shader.bind();
        GlUniform chunkOffset = shader.chunkOffset;

        while (true) {
            if (notTranslucent) {
                if (!it.hasNext()) break;
            } else if (!it.hasPrevious()) {
                break;
            }

            ChunkBuilder.BuiltChunk builtChunk = notTranslucent
                    ? (ChunkBuilder.BuiltChunk) it.next()
                    : (ChunkBuilder.BuiltChunk) it.previous();
            if (!builtChunk.getData().isEmpty(layer)) {
                VertexBuffer vbo = builtChunk.getBuffer(layer);
                BlockPos origin = builtChunk.getOrigin();

                if (chunkOffset != null) {
                    chunkOffset.set((float) (origin.getX() - x),
                            (float) (origin.getY() - y),
                            (float) (origin.getZ() - z));
                    chunkOffset.upload();
                }

                vbo.bind();
                vbo.draw();
            }
        }

        if (chunkOffset != null) {
            chunkOffset.set(0, 0, 0);
        }

        shader.unbind();
        VertexBuffer.unbind();
        client.getProfiler().pop();
        layer.endDrawing();
    }
}
