package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.ConveyorBlock;
import com.zundrel.conveyance.common.blocks.VerticalConveyorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static void init() {

    }

    private static <T extends Item> T register(String name, T item) {
        Registry.register(Registry.ITEM, new Identifier(Conveyance.MODID, name), item);

        return item;
    }
}
