package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.DebugRendererInfoManager;
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
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        world.updateNeighbor(blockPos, this, blockPos);

        world.updateNeighbor(blockPos.offset(blockState.get(FACING)).up(), this, blockPos);
        world.updateNeighbor(blockPos.offset(blockState.get(FACING).getOpposite()).up(), this, blockPos);
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        world.updateNeighbor(blockPos.offset(blockState.get(FACING)).up(), this, blockPos);
        world.updateNeighbor(blockPos.offset(blockState.get(FACING).getOpposite()).up(), this, blockPos);

        super.onBlockRemoved(blockState, world, blockPos, blockState2, boolean_1);
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos fromPos, boolean boolean_1) {
        BlockState newState = blockState;
        Direction direction = newState.get(FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        BlockPos frontPos = blockPos.offset(direction.getOpposite());
        BlockPos downPos = blockPos.down();
        BlockPos conveyorPos = blockPos.offset(direction).up();

        if (world.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world.getBlockState(frontPos).get(FACING) == direction.getOpposite())
            newState = newState.with(ConveyorProperties.FRONT, true);
        else
            newState = newState.with(ConveyorProperties.FRONT, false);

        if (world.getBlockState(downPos).getBlock() instanceof DownVerticalConveyorBlock && world.getBlockState(downPos).get(FACING) == direction)
            conveyorBlockEntity.setDown(true);
        else
            conveyorBlockEntity.setDown(false);

        if (world.getBlockState(blockPos.up()).isAir() && world.getBlockState(conveyorPos).getBlock() instanceof ConveyorBlock && world.getBlockState(conveyorPos).get(FACING) == direction.getOpposite())
            newState = newState.with(ConveyorProperties.CONVEYOR, true);
        else
            newState = newState.with(ConveyorProperties.CONVEYOR, false);

        world.setBlockState(blockPos, newState);

        DebugRendererInfoManager.sendNeighborUpdate(world, blockPos);
    }
}
