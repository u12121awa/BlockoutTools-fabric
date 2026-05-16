package me.maceaura.blockouttools.client.model;

import me.maceaura.blockouttools.block.impl.RampBlock;
import me.maceaura.blockouttools.data.property.SlopeType;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RampBakedModel implements BakedModel, FabricBakedModel {

    private final Sprite sideSprite;
    private final Sprite topSprite;
    private final ModelTransformation transformation;
    private final Map<Long, Mesh> meshCache = new ConcurrentHashMap<>();

    public RampBakedModel(Sprite sideSprite, Sprite topSprite, ModelTransformation transformation) {
        this.sideSprite = sideSprite;
        this.topSprite = topSprite;
        this.transformation = transformation;
    }

    private static final RenderMaterial GUI_MATERIAL;

    static {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        GUI_MATERIAL = renderer.materialFinder()
                .blendMode(0, BlendMode.TRANSLUCENT)
                .find();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        Mesh mesh = meshCache.computeIfAbsent(-1L, k -> {
            Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            MeshBuilder builder = renderer.meshBuilder();
            buildBottomRampItem(builder.getEmitter());
            return builder.build();
        });

        context.meshConsumer().accept(mesh);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
                               Supplier<Random> randomSupplier, RenderContext context) {
        if (state == null) return;
        Direction facing = state.get(RampBlock.FACING);
        SlopeType type = state.get(RampBlock.TYPE);

        long key = ((long) facing.ordinal() << 4) | type.ordinal();
        Mesh mesh = meshCache.computeIfAbsent(key, k -> buildMesh(facing, type));
        context.meshConsumer().accept(mesh);
    }

    private void buildBottomRampItem(QuadEmitter emitter) {
        emitter.material(GUI_MATERIAL);
        setVertex(emitter, 0, 1, 0, 1, Direction.NORTH);
        setVertex(emitter, 1, 1, 1, 0, Direction.NORTH);
        setVertex(emitter, 2, 0, 1, 0, Direction.NORTH);
        setVertex(emitter, 3, 0, 0, 1, Direction.NORTH);

        emitter.nominalFace(Direction.NORTH);
        emitter.spriteBake(0, topSprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();


        emitter.material(null);
        setVertex(emitter, 0, 0, 0, 1, Direction.DOWN);
        setVertex(emitter, 1, 0, 0, 0, Direction.DOWN);
        setVertex(emitter, 2, 1, 0, 0, Direction.DOWN);
        setVertex(emitter, 3, 1, 0, 1, Direction.DOWN);

        emitter.nominalFace(Direction.DOWN);
        emitter.spriteBake(0, topSprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        setVertex(emitter, 0, 1, 1, 0, Direction.NORTH);
        setVertex(emitter, 1, 1, 0, 0, Direction.NORTH);
        setVertex(emitter, 2, 0, 0, 0, Direction.NORTH);
        setVertex(emitter, 3, 0, 1, 0, Direction.NORTH);

        emitter.nominalFace(Direction.NORTH);
        emitter.spriteBake(0, sideSprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        emitter.material(GUI_MATERIAL);
        setVertex(emitter, 0, 1, 0, 0, Direction.EAST);
        setVertex(emitter, 1, 1, 1, 0, Direction.EAST);
        setVertex(emitter, 2, 1, 0, 1, Direction.EAST);
        setVertex(emitter, 3, 1, 0, 1, Direction.EAST);

        emitter.nominalFace(Direction.EAST);
        emitter.spriteBake(0, sideSprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        setVertex(emitter, 0, 0, 1, 0, Direction.WEST);
        setVertex(emitter, 1, 0, 0, 0, Direction.WEST);
        setVertex(emitter, 2, 0, 0, 1, Direction.WEST);
        setVertex(emitter, 3, 0, 0, 1, Direction.WEST);

        emitter.nominalFace(Direction.WEST);
        emitter.spriteBake(0, sideSprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();
    }

    private Mesh buildMesh(Direction facing, SlopeType type) {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        switch (type) {
            case BOTTOM -> buildBottomRamp(emitter, facing);
            case TOP -> buildTopRamp(emitter, facing);
            case HORIZONTAL -> buildHorizontalRamp(emitter, facing);
        }
        return builder.build();
    }

    private void buildBottomRamp(QuadEmitter emitter, Direction facing) {
        switch (facing) {
            case SOUTH -> {
                emitQuad(emitter, topSprite, Direction.UP, 0,0,0, 0,1,1, 1,1,1, 1,0,0);
                emitQuad(emitter, topSprite, Direction.DOWN, 0,0,1, 0,0,0, 1,0,0, 1,0,1);
                emitQuad(emitter, sideSprite, Direction.SOUTH, 0,1,1, 0,0,1, 1,0,1, 1,1,1);
                emitTriangle(emitter, sideSprite, Direction.WEST, 0,0,1, 0,1,1, 0,0,0);
                emitTriangle(emitter, sideSprite, Direction.EAST, 1,1,1, 1,0,1, 1,0,0);
            }
            case EAST -> {
                emitQuad(emitter, topSprite, Direction.UP, 0,0,1, 1,1,1, 1,1,0, 0,0,0);
                emitQuad(emitter, topSprite, Direction.DOWN, 0,0,1, 0,0,0, 1,0,0, 1,0,1);
                emitQuad(emitter, sideSprite, Direction.EAST, 1,1,1, 1,0,1, 1,0,0, 1,1,0);
                emitTriangle(emitter, sideSprite, Direction.SOUTH, 1,0,1, 1,1,1, 0,0,1);
                emitTriangle(emitter, sideSprite, Direction.NORTH, 1,1,0, 1,0,0, 0,0,0);
            }
            case WEST -> {
                emitQuad(emitter, topSprite, Direction.UP, 1,0,0, 0,1,0, 0,1,1, 1,0,1);
                emitQuad(emitter, topSprite, Direction.DOWN, 0,0,1, 0,0,0, 1,0,0, 1,0,1);
                emitQuad(emitter, sideSprite, Direction.WEST, 0,1,0, 0,0,0, 0,0,1, 0,1,1);
                emitTriangle(emitter, sideSprite, Direction.NORTH, 0,0,0, 0,1,0, 1,0,0);
                emitTriangle(emitter, sideSprite, Direction.SOUTH, 0,1,1, 0,0,1, 1,0,1);
            }
            default -> {
                emitQuad(emitter, topSprite, Direction.NORTH, 1,0,1, 1,1,0, 0,1,0, 0,0,1);
                emitQuad(emitter, topSprite, Direction.DOWN, 0,0,1, 0,0,0, 1,0,0, 1,0,1);
                emitQuad(emitter, sideSprite, Direction.NORTH, 1,1,0, 1,0,0, 0,0,0, 0,1,0);
                emitTriangle(emitter, sideSprite, Direction.EAST, 1,0,0, 1,1,0, 1,0,1);
                emitTriangle(emitter, sideSprite, Direction.WEST, 0,1,0, 0,0,0, 0,0,1);
            }
        }
    }

    private void buildTopRamp(QuadEmitter emitter, Direction facing) {
        switch (facing) {
            case SOUTH -> {
                emitQuad(emitter, topSprite, Direction.DOWN, 1,1,0, 1,0,1, 0,0,1, 0,1,0);
                emitQuad(emitter, topSprite, Direction.UP, 0,1,0, 0,1,1, 1,1,1, 1,1,0);
                emitQuad(emitter, sideSprite, Direction.SOUTH, 0,1,1, 0,0,1, 1,0,1, 1,1,1);
                emitTriangle(emitter, sideSprite, Direction.WEST, 0,0,1, 0,1,1, 0,1,0);
                emitTriangle(emitter, sideSprite, Direction.EAST, 1,1,1, 1,0,1, 1,1,0);
            }
            case EAST -> {
                emitQuad(emitter, topSprite, Direction.DOWN, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1);
                emitQuad(emitter, topSprite, Direction.UP, 0,1,0, 0,1,1, 1,1,1, 1,1,0);
                emitQuad(emitter, sideSprite, Direction.EAST, 1,1,1, 1,0,1, 1,0,0, 1,1,0);
                emitTriangle(emitter, sideSprite, Direction.SOUTH, 1,0,1, 1,1,1, 0,1,1);
                emitTriangle(emitter, sideSprite, Direction.NORTH, 1,1,0, 1,0,0, 0,1,0);
            }
            case WEST -> {
                emitQuad(emitter, topSprite, Direction.DOWN, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0);
                emitQuad(emitter, topSprite, Direction.UP, 0,1,0, 0,1,1, 1,1,1, 1,1,0);
                emitQuad(emitter, sideSprite, Direction.WEST, 0,1,0, 0,0,0, 0,0,1, 0,1,1);
                emitTriangle(emitter, sideSprite, Direction.NORTH, 0,0,0, 0,1,0, 1,1,0);
                emitTriangle(emitter, sideSprite, Direction.SOUTH, 0,1,1, 0,0,1, 1,1,1);
            }
            default -> {
                emitQuad(emitter, topSprite, Direction.SOUTH, 0,1,1, 0,0,0, 1,0,0, 1,1,1);
                emitQuad(emitter, topSprite, Direction.UP, 0,1,0, 0,1,1, 1,1,1, 1,1,0);
                emitQuad(emitter, sideSprite, Direction.NORTH, 1,1,0, 1,0,0, 0,0,0, 0,1,0);
                emitTriangle(emitter, sideSprite, Direction.WEST, 0,0,0, 0,1,1, 0,1,0);
                emitTriangle(emitter, sideSprite, Direction.EAST, 1,1,1, 1,0,0, 1,1,0);
            }
        }
    }

    private void buildHorizontalRamp(QuadEmitter emitter, Direction facing) {
        switch (facing) {
            case SOUTH -> {
                emitQuad(emitter, topSprite, Direction.EAST, 0,0,0, 0,1,0, 1,1,1, 1,0,1);
                emitQuad(emitter, sideSprite, Direction.SOUTH, 0,1,1, 0,0,1, 1,0,1, 1,1,1);
                emitQuad(emitter, sideSprite, Direction.WEST, 0,1,0, 0,0,0, 0,0,1, 0,1,1);
                emitTriangle(emitter, sideSprite, Direction.DOWN, 0,0,1, 0,0,0, 1,0,1);
                emitTriangle(emitter, sideSprite, Direction.UP, 0,1,0, 0,1,1, 1,1,1);
            }
            case EAST -> {
                emitQuad(emitter, topSprite, Direction.NORTH, 0,0,1, 0,1,1, 1,1,0, 1,0,0);
                emitQuad(emitter, sideSprite, Direction.EAST, 1,1,1, 1,0,1, 1,0,0, 1,1,0);
                emitQuad(emitter, sideSprite, Direction.SOUTH, 0,1,1, 0,0,1, 1,0,1, 1,1,1);
                emitTriangle(emitter, sideSprite, Direction.DOWN, 1,0,1, 0,0,1, 1,0,0);
                emitTriangle(emitter, sideSprite, Direction.UP, 0,1,1, 1,1,1, 1,1,0);
            }
            case WEST -> {
                emitQuad(emitter, topSprite, Direction.SOUTH, 1,0,0, 1,1,0, 0,1,1, 0,0,1);
                emitQuad(emitter, sideSprite, Direction.WEST, 0,1,0, 0,0,0, 0,0,1, 0,1,1);
                emitQuad(emitter, sideSprite, Direction.NORTH, 1,1,0, 1,0,0, 0,0,0, 0,1,0);
                emitTriangle(emitter, sideSprite, Direction.DOWN, 0,0,0, 1,0,0, 0,0,1);
                emitTriangle(emitter, sideSprite, Direction.UP, 1,1,0, 0,1,0, 0,1,1);
            }
            default -> {
                emitQuad(emitter, topSprite, Direction.WEST, 1,0,1, 1,1,1, 0,1,0, 0,0,0);
                emitQuad(emitter, sideSprite, Direction.NORTH, 1,1,0, 1,0,0, 0,0,0, 0,1,0);
                emitQuad(emitter, sideSprite, Direction.EAST, 1,1,1, 1,0,1, 1,0,0, 1,1,0);
                emitTriangle(emitter, sideSprite, Direction.DOWN, 1,0,0, 1,0,1, 0,0,0);
                emitTriangle(emitter, sideSprite, Direction.UP, 1,1,1, 1,1,0, 0,1,0);
            }
        }
    }


    private void emitQuad(QuadEmitter emitter, Sprite sprite, Direction nominalFace,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3) {
        setVertex(emitter, 0, x0, y0, z0, nominalFace);
        setVertex(emitter, 1, x1, y1, z1, nominalFace);
        setVertex(emitter, 2, x2, y2, z2, nominalFace);
        setVertex(emitter, 3, x3, y3, z3, nominalFace);

        emitter.nominalFace(nominalFace);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();
    }

    private void emitTriangle(QuadEmitter emitter, Sprite sprite, Direction nominalFace,
                              float x0, float y0, float z0,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2) {
        setVertex(emitter, 0, x0, y0, z0, nominalFace);
        setVertex(emitter, 1, x1, y1, z1, nominalFace);
        setVertex(emitter, 2, x2, y2, z2, nominalFace);
        setVertex(emitter, 3, x2, y2, z2, nominalFace);

        emitter.nominalFace(nominalFace);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();
    }

    private void setVertex(QuadEmitter emitter, int vertexIndex, float x, float y, float z, Direction face) {
        emitter.pos(vertexIndex, x, y, z);
        float u = 0, v = 0;
        switch (face) {
            case UP ->    { u = x; v = z; }
            case DOWN ->  { u = x; v = 1f - z; }
            case NORTH -> { u = 1f - x; v = 1f - y; }
            case SOUTH -> { u = x; v = 1f - y; }
            case WEST ->  { u = z; v = 1f - y; }
            case EAST ->  { u = 1f - z; v = 1f - y; }
        }
        emitter.sprite(vertexIndex, 0, u, v);
    }

    @Override public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) { return Collections.emptyList(); }
    @Override public boolean useAmbientOcclusion() { return true; }
    @Override public boolean hasDepth() { return false; }
    @Override public boolean isSideLit() { return true; }
    @Override public boolean isBuiltin() { return false; }
    @Override public Sprite getParticleSprite() { return sideSprite; }
    @Override public ModelTransformation getTransformation() { return transformation; }
    @Override public ModelOverrideList getOverrides() { return ModelOverrideList.EMPTY; }
}