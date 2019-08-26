package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.ConveyorBlock;
import com.zundrel.conveyance.common.blocks.VerticalConveyorBlock;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {
    public static BlockEntityType CONVEYOR;
    public static BlockEntityType VERTICAL_CONVEYOR;

    public static void init() {
        CONVEYOR = registerConveyor("conveyor", ModBlocks.CONVEYOR);
        VERTICAL_CONVEYOR = registerVerticalConveyor("vertical_conveyor", ModBlocks.VERTICAL_CONVEYOR);
    }

    private static BlockEntityType registerConveyor(String name, Block... block) {
        return Registry.register(Registry.BLOCK_ENTITY, new Identifier(Conveyance.MODID, name), BlockEntityType.Builder.create(ConveyorBlockEntity::new, block).build(null));
    }

    private static BlockEntityType registerVerticalConveyor(String name, Block... block) {
        return Registry.register(Registry.BLOCK_ENTITY, new Identifier(Conveyance.MODID, name), BlockEntityType.Builder.create(VerticalConveyorBlockEntity::new, block).build(null));
    }
}
