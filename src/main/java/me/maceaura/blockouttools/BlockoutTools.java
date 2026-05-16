package me.maceaura.blockouttools;

import me.maceaura.blockouttools.block.BlockoutBlocks;
import net.fabricmc.api.ModInitializer;

public class BlockoutTools implements ModInitializer {

    public static final String MOD_ID = "blockouttools";

    @Override
    public void onInitialize() {
        BlockoutBlocks.register();
    }
}
