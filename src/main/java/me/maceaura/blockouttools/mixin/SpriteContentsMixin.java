package me.maceaura.blockouttools.mixin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Mixin(SpriteContents.class)
public abstract class SpriteContentsMixin {

    @Unique
    private static final Logger BLOCKOUT_LOGGER = LoggerFactory.getLogger("BlockoutTools/Mipmap");

    @Unique
    private static final Map<String, String> MIPMAP_TEXTURES = new HashMap<>();

    static {
        MIPMAP_TEXTURES.put("blockouttools:block/blockout_grid", "blockout_grid");
        MIPMAP_TEXTURES.put("blockouttools:block/blockout_grid_top", "blockout_grid_top");
    }

    @Shadow
    @Final
    private Identifier id;

    @Shadow
    @Final
    int width;

    @Shadow
    @Final
    int height;

    @Shadow
    NativeImage[] mipmapLevelsImages;

    @Inject(method = "generateMipmaps", at = @At("RETURN"))
    private void blockouttools_afterGenerateMipmaps(int mipmapLevels, CallbackInfo ci) {
        if (this.mipmapLevelsImages == null) {
            return;
        }

        int baseSize = Math.min(this.width, this.height);
        if (baseSize < 2) {
            return;
        }

        int fullMipLevels = (int) (Math.log(baseSize) / Math.log(2));
        int targetArrayLength = fullMipLevels + 1;

        if (targetArrayLength > this.mipmapLevelsImages.length) {
            NativeImage[] expanded = new NativeImage[targetArrayLength];
            System.arraycopy(this.mipmapLevelsImages, 0, expanded, 0, this.mipmapLevelsImages.length);

            for (int level = this.mipmapLevelsImages.length; level < targetArrayLength; level++) {
                int mipWidth = this.width >> level;
                int mipHeight = this.height >> level;
                if (mipWidth < 1) mipWidth = 1;
                if (mipHeight < 1) mipHeight = 1;
                expanded[level] = blockouttools_downsample(expanded[level - 1], mipWidth, mipHeight);
            }

            this.mipmapLevelsImages = expanded;
        }

        String textureId = this.id.toString();
        String mipmapPrefix = MIPMAP_TEXTURES.get(textureId);

        if (mipmapPrefix == null) {
            return;
        }

        BLOCKOUT_LOGGER.info("正在为 {} 加载自定义 mipmap (数组长度 {}, 基础尺寸 {}x{})",
                textureId, this.mipmapLevelsImages.length, this.width, this.height);

        int replacedCount = 0;

        for (int level = 1; level < this.mipmapLevelsImages.length; level++) {
            int mipSize = this.width >> level;
            if (mipSize < 1) mipSize = 1;

            String resourcePath = "/assets/blockouttools/textures/mipmap/"
                    + mipmapPrefix + "_" + mipSize + "x" + mipSize + ".png";

            try {
                InputStream stream = SpriteContentsMixin.class.getResourceAsStream(resourcePath);
                if (stream != null) {
                    NativeImage customMipmap = NativeImage.read(stream);
                    stream.close();

                    if (customMipmap.getWidth() == mipSize && customMipmap.getHeight() == mipSize) {
                        if (this.mipmapLevelsImages[level] != null) {
                            this.mipmapLevelsImages[level].close();
                        }
                        this.mipmapLevelsImages[level] = customMipmap;
                        replacedCount++;
                    } else {
                        customMipmap.close();
                    }
                }
            } catch (IOException e) {
            }
        }

        BLOCKOUT_LOGGER.info("{} 自定义 mipmap 完成: {} 级替换", textureId, replacedCount);
    }

    @Unique
    private static NativeImage blockouttools_downsample(NativeImage source, int targetWidth, int targetHeight) {
        if (source == null) {
            return new NativeImage(targetWidth, targetHeight, true);
        }

        NativeImage result = new NativeImage(targetWidth, targetHeight, false);
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int srcX = Math.min(x * 2, srcWidth - 1);
                int srcY = Math.min(y * 2, srcHeight - 1);
                int srcX1 = Math.min(srcX + 1, srcWidth - 1);
                int srcY1 = Math.min(srcY + 1, srcHeight - 1);

                int c00 = source.getColor(srcX, srcY);
                int c10 = source.getColor(srcX1, srcY);
                int c01 = source.getColor(srcX, srcY1);
                int c11 = source.getColor(srcX1, srcY1);

                int a = ((c00 >> 24 & 0xFF) + (c10 >> 24 & 0xFF) + (c01 >> 24 & 0xFF) + (c11 >> 24 & 0xFF)) / 4;
                int b = ((c00 >> 16 & 0xFF) + (c10 >> 16 & 0xFF) + (c01 >> 16 & 0xFF) + (c11 >> 16 & 0xFF)) / 4;
                int g = ((c00 >> 8 & 0xFF) + (c10 >> 8 & 0xFF) + (c01 >> 8 & 0xFF) + (c11 >> 8 & 0xFF)) / 4;
                int r = ((c00 & 0xFF) + (c10 & 0xFF) + (c01 & 0xFF) + (c11 & 0xFF)) / 4;

                result.setColor(x, y, (a << 24) | (b << 16) | (g << 8) | r);
            }
        }

        return result;
    }
}
