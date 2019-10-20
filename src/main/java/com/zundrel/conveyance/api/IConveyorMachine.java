package com.zundrel.conveyance.api;

import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IConveyorMachine {
    void insert(World world, BlockPos pos, BlockState state, ConveyorBlockEntity blockEntity, ItemStack stack, Direction direction);

    boolean canInsert(World world, BlockPos pos, BlockState state, ConveyorBlockEntity blockEntity, ItemStack stack, Direction direction);
}
