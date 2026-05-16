package me.maceaura.blockouttools.mixin;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin {

    @Unique
    private static final Logger BLOCKOUT_ATLAS_LOGGER = LoggerFactory.getLogger("BlockoutTools/Atlas");

    @Unique
    private static final int TARGET_MIPMAP_LEVEL = 11;

    @Shadow
    @Final
    private Identifier id;

    @ModifyArg(
            method = "upload",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/TextureUtil;prepareImage(IIII)V"),
            index = 1
    )
    private int blockouttools_increaseMipLevels(int originalMipLevel) {
        if ("minecraft:textures/atlas/blocks.png".equals(this.id.toString())) {
            int newMipLevel = Math.max(originalMipLevel, TARGET_MIPMAP_LEVEL);
            BLOCKOUT_ATLAS_LOGGER.info("图集 {} prepareImage mipLevel: {} -> {}",
                    this.id, originalMipLevel, newMipLevel);
            return newMipLevel;
        }
        return originalMipLevel;
    }

    @Inject(method = "upload", at = @At("RETURN"))
    private void blockouttools_afterUpload(SpriteLoader.StitchResult stitchResult, CallbackInfo ci) {
        if (!"minecraft:textures/atlas/blocks.png".equals(this.id.toString())) {
            return;
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, TARGET_MIPMAP_LEVEL);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

        BLOCKOUT_ATLAS_LOGGER.info("图集 {} mipmap 生成完成（glGenerateMipmap），级别: {}",
                this.id, TARGET_MIPMAP_LEVEL);
    }
}
