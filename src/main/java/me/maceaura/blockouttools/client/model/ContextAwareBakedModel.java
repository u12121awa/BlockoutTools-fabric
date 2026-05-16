package me.maceaura.blockouttools.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContextAwareBakedModel implements BakedModel {

    private final BakedModel handModel;
    private final BakedModel guiModel;

    private static final ThreadLocal<Boolean> IS_GUI_CONTEXT = ThreadLocal.withInitial(() -> false);

    public ContextAwareBakedModel(BakedModel handModel, BakedModel guiModel) {
        this.handModel = handModel;
        this.guiModel = guiModel;
    }

    public static void setGuiContext(boolean isGui) {
        IS_GUI_CONTEXT.set(isGui);
    }

    public static boolean isGuiContext() {
        return IS_GUI_CONTEXT.get();
    }

    private BakedModel getCurrentModel() {
        return IS_GUI_CONTEXT.get() ? guiModel : handModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return getCurrentModel().getQuads(state, face, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return handModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return handModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return handModel.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return handModel.isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return handModel.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return handModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return handModel.getOverrides();
    }
}
