package me.maceaura.blockouttools.mixin;

import me.maceaura.blockouttools.block.BlockoutToolsBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayers.class)
public abstract class ChunkBuilderMixin {

    @Inject(
            method = "getBlockLayer",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void blockouttools_redirectBlockLayer(BlockState state, CallbackInfoReturnable<RenderLayer> cir) {
        if (state.getBlock() instanceof BlockoutToolsBlock) {
            cir.setReturnValue(RenderLayer.getCutoutMipped());
        }
    }
}
