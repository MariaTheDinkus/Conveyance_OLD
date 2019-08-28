package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static BlockEntityType CONVEYOR;
    public static BlockEntityType VERTICAL_CONVEYOR;
    public static BlockEntityType DOWN_VERTICAL_CONVEYOR;

    public static void init() {
        CONVEYOR = register("conveyor", ConveyorBlockEntity::new, ModBlocks.CONVEYOR);
        VERTICAL_CONVEYOR = register("vertical_conveyor", VerticalConveyorBlockEntity::new, ModBlocks.VERTICAL_CONVEYOR);
        DOWN_VERTICAL_CONVEYOR = register("down_vertical_conveyor", DownVerticalConveyorBlockEntity::new, ModBlocks.DOWN_VERTICAL_CONVEYOR);
    }

    private static BlockEntityType register(String name, Supplier<BlockEntity> blockEntity, Block... block) {
        return Registry.register(Registry.BLOCK_ENTITY, new Identifier(Conveyance.MODID, name), BlockEntityType.Builder.create(blockEntity, block).build(null));
    }
}
