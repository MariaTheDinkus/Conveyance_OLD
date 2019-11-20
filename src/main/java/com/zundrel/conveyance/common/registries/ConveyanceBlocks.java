package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.conveyors.*;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConveyanceBlocks {
    public static ConveyorBlock CONVEYOR = register("conveyor", new ConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 16));
    public static VerticalConveyorBlock VERTICAL_CONVEYOR = register("conveyor_vertical", new VerticalConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 16));
    public static DownVerticalConveyorBlock DOWN_VERTICAL_CONVEYOR = register("conveyor_vertical_down", new DownVerticalConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 16));
    public static ConveyorBlock FAST_CONVEYOR = register("fast_conveyor", new ConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 8));
    public static VerticalConveyorBlock VERTICAL_FAST_CONVEYOR = register("fast_conveyor_vertical", new VerticalConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 8));
    public static DownVerticalConveyorBlock DOWN_VERTICAL_FAST_CONVEYOR = register("fast_conveyor_vertical_down", new DownVerticalConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 8));
    public static ConveyorBlock EXPRESS_CONVEYOR = register("express_conveyor", new ConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 4));
    public static VerticalConveyorBlock VERTICAL_EXPRESS_CONVEYOR = register("express_conveyor_vertical", new VerticalConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 4));
    public static DownVerticalConveyorBlock DOWN_VERTICAL_EXPRESS_CONVEYOR = register("express_conveyor_vertical_down", new DownVerticalConveyorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build(), 4));

    public static AlternatorBlock ALTERNATOR = register("alternator", new AlternatorBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()));
    public static SplitterBlock SPLITTER = register("splitter", new SplitterBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()));

    public static void init() {

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
