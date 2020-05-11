package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.conveyors.*;
import com.zundrel.conveyance.common.blocks.decor.CatwalkBlock;
import com.zundrel.conveyance.common.blocks.decor.CatwalkStairsBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConveyanceBlocks {
	public static AlternatorBlock ALTERNATOR = register("alternator", new AlternatorBlock(FabricBlockSettings.copy(Blocks.STONE)));
	public static SplitterBlock SPLITTER = register("splitter", new SplitterBlock(FabricBlockSettings.copy(Blocks.STONE)));
	public static InserterBlock INSERTER = register("inserter", new InserterBlock("normal", 16, FabricBlockSettings.copy(Blocks.STONE).nonOpaque()));
	public static InserterBlock INSERTER_FAST = register("inserter_fast", new InserterBlock("fast", 8, FabricBlockSettings.copy(Blocks.STONE).nonOpaque()));

    public static ConveyorBlock CONVEYOR = register("conveyor", new ConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 16));
    public static VerticalConveyorBlock VERTICAL_CONVEYOR = register("conveyor_vertical", new VerticalConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 16));
    public static DownVerticalConveyorBlock DOWN_VERTICAL_CONVEYOR = register("conveyor_vertical_down", new DownVerticalConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 16));
    public static ConveyorBlock FAST_CONVEYOR = register("fast_conveyor", new ConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 8));
    public static VerticalConveyorBlock VERTICAL_FAST_CONVEYOR = register("fast_conveyor_vertical", new VerticalConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 8));
    public static DownVerticalConveyorBlock DOWN_VERTICAL_FAST_CONVEYOR = register("fast_conveyor_vertical_down", new DownVerticalConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 8));
    public static ConveyorBlock EXPRESS_CONVEYOR = register("express_conveyor", new ConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 4));
    public static VerticalConveyorBlock VERTICAL_EXPRESS_CONVEYOR = register("express_conveyor_vertical", new VerticalConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 4));
    public static DownVerticalConveyorBlock DOWN_VERTICAL_EXPRESS_CONVEYOR = register("express_conveyor_vertical_down", new DownVerticalConveyorBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque(), 4));

    public static CatwalkBlock CATWALK = register("catwalk", new CatwalkBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static CatwalkStairsBlock CATWALK_STAIRS = register("catwalk_stairs", new CatwalkStairsBlock(FabricBlockSettings.copy(Blocks.STONE).sounds(BlockSoundGroup.METAL).nonOpaque()));

    public static void init() {

    }
    
    @Environment(EnvType.CLIENT)
    public static void registerRenderLayers() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ALTERNATOR, SPLITTER, CONVEYOR, VERTICAL_CONVEYOR, DOWN_VERTICAL_CONVEYOR,
                FAST_CONVEYOR, VERTICAL_FAST_CONVEYOR, DOWN_VERTICAL_FAST_CONVEYOR,
                EXPRESS_CONVEYOR, VERTICAL_EXPRESS_CONVEYOR, DOWN_VERTICAL_EXPRESS_CONVEYOR);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), CATWALK, CATWALK_STAIRS);
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
