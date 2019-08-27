package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.ConveyorBlock;
import com.zundrel.conveyance.common.blocks.VerticalConveyorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
    public static ConveyorBlock CONVEYOR;
    public static VerticalConveyorBlock VERTICAL_CONVEYOR;

    public static void init() {
        CONVEYOR = register("conveyor", new ConveyorBlock(Block.Settings.copy(Blocks.IRON_BLOCK)));
        VERTICAL_CONVEYOR = register("conveyor_vertical", new VerticalConveyorBlock(Block.Settings.copy(Blocks.IRON_BLOCK)));
    }

    private static BlockItem createBlockItem(Block block) {
        return createBlockItem(block, Conveyance.generalItemGroup);
    }

    private static BlockItem createBlockItem(Block block, ItemGroup group) {
        return new BlockItem(block, new Item.Settings().group(group));
    }

    public static <T extends Block> T register(String name, T block) {
        Registry.register(Registry.BLOCK, new Identifier(Conveyance.MODID, name), block);
        Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), createBlockItem(block));

        return block;
    }

    public static <T extends Block> T register(String name, T block, BlockItem blockItem) {
        Registry.register(Registry.BLOCK, new Identifier(Conveyance.MODID, name), block);
        Registry.register(Registry.ITEM, new Identifier(Conveyance.MODID, name), blockItem);

        return block;
    }

    public static <T extends Block> T register(String name, T block, String itemName, BlockItem blockItem) {
        Registry.register(Registry.BLOCK, new Identifier(Conveyance.MODID, name), block);
        Registry.register(Registry.ITEM, new Identifier(Conveyance.MODID, itemName), blockItem);

        return block;
    }
}
