package me.maceaura.blockouttools.mixin;

import me.maceaura.blockouttools.client.model.ContextAwareBakedModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"))
    private void blockouttools_beforeRenderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
                                                 MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                                 int light, int overlay, BakedModel model, CallbackInfo ci) {
        boolean isGui = renderMode == ModelTransformationMode.GUI;
        ContextAwareBakedModel.setGuiContext(isGui);
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("RETURN"))
    private void blockouttools_afterRenderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
                                                MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                                int light, int overlay, BakedModel model, CallbackInfo ci) {
        ContextAwareBakedModel.setGuiContext(false);
    }
}
