package me.maceaura.blockouttools.client.render;

import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

/**
 * 自定义渲染层，绑定独立的高 Mipmap 纹理。
 * 用于将 blockout 方块的渲染从原版 blocks.png 图集中隔离出来。
 *
 * 继承 RenderLayer 以获取对 protected MultiPhaseParameters 的访问权限。
 */
public class BlockoutRenderLayers extends RenderLayer {

    // 独立纹理的 Identifier（注册到 TextureManager 时使用）
    public static final Identifier BLOCKOUT_SIDE_TEXTURE_ID =
            new Identifier("blockouttools", "standalone/blockout_grid");
    public static final Identifier BLOCKOUT_TOP_TEXTURE_ID =
            new Identifier("blockouttools", "standalone/blockout_grid_top");

    // 自定义渲染层：侧面纹理
    public static final RenderLayer BLOCKOUT_SOLID_SIDE = createBlockoutLayer(
            "blockout_solid_side", BLOCKOUT_SIDE_TEXTURE_ID);

    // 自定义渲染层：顶面纹理
    public static final RenderLayer BLOCKOUT_SOLID_TOP = createBlockoutLayer(
            "blockout_solid_top", BLOCKOUT_TOP_TEXTURE_ID);

    // 私有构造函数，仅用于满足 RenderLayer 的继承要求
    private BlockoutRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode,
                                  int expectedBufferSize, boolean hasCrumbling, boolean translucent,
                                  Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    /**
     * 创建一个自定义的 terrain 渲染层，绑定指定的独立纹理。
     * 使用与原版 solid terrain 相同的顶点格式和着色器，
     * 但纹理绑定指向我们的独立 GL 纹理对象。
     */
    private static RenderLayer createBlockoutLayer(String name, Identifier textureId) {
        // 使用 rendertype_solid 着色器（与原版 solid terrain 一致）
        MultiPhaseParameters parameters = MultiPhaseParameters.builder()
                .program(new ShaderProgram(GameRenderer::getRenderTypeSolidProgram))
                .texture(new RenderPhase.Texture(textureId, false, true))  // blur=false, mipmap=true
                .transparency(NO_TRANSPARENCY)
                .lightmap(ENABLE_LIGHTMAP)
                .build(true);

        return RenderLayer.of(
                name,
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS,
                2097152,  // 与原版 chunk buffer 大小一致
                true,     // hasCrumbling
                false,    // translucent
                parameters
        );
    }

    /**
     * 根据面的方向获取对应的渲染层
     * @param isTopOrBottom 是否为顶面/底面
     */
    public static RenderLayer getLayerForFace(boolean isTopOrBottom) {
        return isTopOrBottom ? BLOCKOUT_SOLID_TOP : BLOCKOUT_SOLID_SIDE;
    }

    /**
     * 获取侧面渲染层（默认，用于 ChunkBuilder 中的 RenderLayer 分发）
     */
    public static RenderLayer getBlockoutSolidLayer() {
        return BLOCKOUT_SOLID_SIDE;
    }
}
