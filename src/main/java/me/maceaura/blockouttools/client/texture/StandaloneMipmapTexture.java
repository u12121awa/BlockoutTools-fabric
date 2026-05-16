package me.maceaura.blockouttools.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 独立的高 Mipmap 纹理类。
 * 将指定的 PNG 纹理作为独立 GL 纹理对象加载到显存中，
 * 并生成 11 级 Mipmap，完全不影响原版图集。
 */
public class StandaloneMipmapTexture extends AbstractTexture {

    private static final Logger LOGGER = LoggerFactory.getLogger("BlockoutTools/StandaloneTexture");
    private static final int TARGET_MIPMAP_LEVELS = 11;

    private final Identifier textureSource;
    private final String mipmapPrefix;

    /**
     * @param textureSource 纹理资源路径，例如 blockouttools:textures/block/blockout_grid.png
     * @param mipmapPrefix  自定义 mipmap 文件前缀，例如 "blockout_grid"
     */
    public StandaloneMipmapTexture(Identifier textureSource, String mipmapPrefix) {
        this.textureSource = textureSource;
        this.mipmapPrefix = mipmapPrefix;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        // 关闭旧纹理（如果有）
        this.clearGlId();

        // 加载基础纹理
        NativeImage baseImage;
        try (InputStream stream = manager.open(this.textureSource)) {
            baseImage = NativeImage.read(stream);
        }

        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        int maxPossibleLevels = (int) (Math.log(Math.min(width, height)) / Math.log(2));
        int mipLevels = Math.min(TARGET_MIPMAP_LEVELS, maxPossibleLevels);

        LOGGER.info("加载独立纹理 {} ({}x{}), mipmap 级别: {}", textureSource, width, height, mipLevels);

        // 生成 GL 纹理并分配存储
        TextureUtil.prepareImage(this.getGlId(), mipLevels, width, height);

        // 设置纹理参数
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipLevels);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // 上传基础级别 (level 0)
        baseImage.upload(0, 0, 0, false);

        // 逐级生成并上传 mipmap
        NativeImage previousLevel = baseImage;
        for (int level = 1; level <= mipLevels; level++) {
            int mipWidth = Math.max(1, width >> level);
            int mipHeight = Math.max(1, height >> level);

            // 尝试加载预生成的自定义 mipmap
            NativeImage mipImage = tryLoadCustomMipmap(manager, level, mipWidth, mipHeight);

            if (mipImage == null) {
                // 使用 box filter 降采样
                mipImage = downsample(previousLevel, mipWidth, mipHeight);
            }

            mipImage.upload(level, 0, 0, false);

            // 释放上一级（除了 level 0 的 baseImage，最后统一释放）
            if (level > 1) {
                previousLevel.close();
            }
            previousLevel = mipImage;
        }

        // 释放最后一级
        if (previousLevel != baseImage) {
            previousLevel.close();
        }
        baseImage.close();

        LOGGER.info("独立纹理 {} 上传完成，共 {} 级 mipmap", textureSource, mipLevels);
    }

    /**
     * 尝试从资源包加载预生成的自定义 mipmap 文件
     */
    private NativeImage tryLoadCustomMipmap(ResourceManager manager, int level, int mipWidth, int mipHeight) {
        // 尝试从 classpath 加载（兼容 jar 内资源）
        String resourcePath = "/assets/blockouttools/textures/mipmap/"
                + mipmapPrefix + "_" + mipWidth + "x" + mipHeight + ".png";

        try {
            InputStream stream = StandaloneMipmapTexture.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                NativeImage image = NativeImage.read(stream);
                stream.close();
                if (image.getWidth() == mipWidth && image.getHeight() == mipHeight) {
                    return image;
                }
                image.close();
            }
        } catch (IOException e) {
            // 忽略，使用自动降采样
        }

        // 也尝试通过 ResourceManager 加载
        Identifier mipId = new Identifier("blockouttools",
                "textures/mipmap/" + mipmapPrefix + "_" + mipWidth + "x" + mipHeight + ".png");
        try {
            InputStream stream = manager.open(mipId);
            NativeImage image = NativeImage.read(stream);
            stream.close();
            if (image.getWidth() == mipWidth && image.getHeight() == mipHeight) {
                return image;
            }
            image.close();
        } catch (Exception e) {
            // 忽略
        }

        return null;
    }

    /**
     * Box filter 降采样：将源图像缩小到目标尺寸
     */
    private static NativeImage downsample(NativeImage source, int targetWidth, int targetHeight) {
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

                // ABGR 格式的 box filter 平均
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
