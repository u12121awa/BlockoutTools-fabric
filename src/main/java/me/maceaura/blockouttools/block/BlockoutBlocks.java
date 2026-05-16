package me.maceaura.blockouttools.block;

import me.maceaura.blockouttools.BlockoutTools;
import me.maceaura.blockouttools.block.impl.BlockoutBoxBlock;
import me.maceaura.blockouttools.block.impl.DirectionalSlabBlock;
import me.maceaura.blockouttools.block.impl.RampBlock;
import me.maceaura.blockouttools.block.impl.StairsLinearBlock;
import me.maceaura.blockouttools.block.impl.VerticalSlabBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BlockoutBlocks {

    public static final Block BLOCKOUT_BOX = new BlockoutBoxBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.WHITE)
                    .strength(1.5f, 6.0f)
                    .sounds(BlockSoundGroup.STONE)
    );

    public static final Block BLOCKOUT_BOX_SLAB = new DirectionalSlabBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.WHITE)
                    .strength(1.5f, 6.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .nonOpaque()
    );

    public static final Block BLOCKOUT_STAIRS_LINEAR = new StairsLinearBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.WHITE)
                    .strength(1.5f, 6.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .nonOpaque()
    );

    public static final Block BLOCKOUT_VERTICAL_SLAB = new VerticalSlabBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.WHITE)
                    .strength(1.5f, 6.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .nonOpaque()
    );

    public static final Block BLOCKOUT_RAMP = new RampBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.WHITE)
                    .strength(1.5f, 6.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .nonOpaque()
    );


    public static final RegistryKey<ItemGroup> BLOCKOUT_GROUP_KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(BlockoutTools.MOD_ID, "blockout_tools"));

    public static final ItemGroup BLOCKOUT_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(BLOCKOUT_BOX))
            .displayName(Text.translatable("itemGroup.blockouttools.blockout_tools"))
            .entries((context, entries) -> {
                entries.add(BLOCKOUT_BOX);
                entries.add(BLOCKOUT_BOX_SLAB);
                entries.add(BLOCKOUT_VERTICAL_SLAB);
                entries.add(BLOCKOUT_STAIRS_LINEAR);
                entries.add(BLOCKOUT_RAMP);
            })
            .build();

    public static void register() {
        registerBlock("blockout_box", BLOCKOUT_BOX);
        registerBlock("blockout_slab", BLOCKOUT_BOX_SLAB);
        registerBlock("blockout_vertical_slab", BLOCKOUT_VERTICAL_SLAB);
        registerBlock("blockout_stairs_linear", BLOCKOUT_STAIRS_LINEAR);
        registerBlock("blockout_ramp", BLOCKOUT_RAMP);

        Registry.register(Registries.ITEM_GROUP, BLOCKOUT_GROUP_KEY, BLOCKOUT_GROUP);
    }

    private static void registerBlock(String name, Block block) {
        Identifier id = new Identifier(BlockoutTools.MOD_ID, name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new FabricItemSettings()));
    }
}
