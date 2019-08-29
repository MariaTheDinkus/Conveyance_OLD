package com.zundrel.conveyance.common.blocks;

import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DownVerticalConveyorBlock extends VerticalConveyorBlock {
    public DownVerticalConveyorBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(ConveyorProperties.FRONT, false).with(ConveyorProperties.CONVEYOR, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new DownVerticalConveyorBlockEntity();
    }

    @Override
    public void onBlockAdded(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1) {
        world_1.updateNeighbor(blockPos_1, this, blockPos_1);

        world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING)).up(), this, blockPos_1);
        world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING).getOpposite()).up(), this, blockPos_1);
    }

    @Override
    public void onBlockRemoved(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1) {
        world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING)).up(), this, blockPos_1);
        world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING).getOpposite()).up(), this, blockPos_1);

        super.onBlockRemoved(blockState_1, world_1, blockPos_1, blockState_2, boolean_1);
    }

    @Override
    public void neighborUpdate(BlockState blockState_1, World world_1, BlockPos blockPos_1, Block block_1, BlockPos blockPos_2, boolean boolean_1) {
        BlockState newState = blockState_1;
        Direction direction = newState.get(FACING);
        DownVerticalConveyorBlockEntity conveyorBlockEntity = (DownVerticalConveyorBlockEntity) world_1.getBlockEntity(blockPos_1);

        BlockPos frontPos = blockPos_1.offset(direction.getOpposite());
        BlockPos downPos = blockPos_1.down();
        BlockPos conveyorPos = blockPos_1.offset(direction).up();

        if (world_1.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world_1.getBlockState(frontPos).get(FACING) == direction.getOpposite())
            newState = newState.with(ConveyorProperties.FRONT, true);
        else
            newState = newState.with(ConveyorProperties.FRONT, false);

        if (world_1.getBlockState(downPos).getBlock() instanceof DownVerticalConveyorBlock && world_1.getBlockState(downPos).get(FACING) == direction) {
            conveyorBlockEntity.setDown(true);
        } else
            conveyorBlockEntity.setDown(false);

        if (world_1.getBlockState(blockPos_1.up()).isAir() && world_1.getBlockState(conveyorPos).getBlock() instanceof ConveyorBlock && world_1.getBlockState(conveyorPos).get(FACING) == direction.getOpposite())
            newState = newState.with(ConveyorProperties.CONVEYOR, true);
        else
            newState = newState.with(ConveyorProperties.CONVEYOR, false);

        world_1.setBlockState(blockPos_1, newState);

        super.neighborUpdate(blockState_1, world_1, blockPos_1, block_1, blockPos_2, boolean_1);
    }
}
