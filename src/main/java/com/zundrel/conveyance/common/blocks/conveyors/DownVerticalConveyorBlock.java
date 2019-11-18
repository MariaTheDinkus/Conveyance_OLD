package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.api.ConveyorType;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DownVerticalConveyorBlock extends VerticalConveyorBlock {
    public DownVerticalConveyorBlock(Settings settings, int speed) {
        super(settings, speed);

        setDefaultState(getDefaultState().with(ConveyorProperties.FRONT, false).with(ConveyorProperties.CONVEYOR, false));
    }

    @Override
    public ConveyorType getType() {
        return ConveyorType.DOWN_VERTICAL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new DownVerticalConveyorBlockEntity();
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
        BlockState newState = blockState.getStateForNeighborUpdate(null, blockState, world, blockPos, blockPos2);
        Direction direction = newState.get(FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        BlockPos downPos = blockPos.down(1);

        if (world.getBlockState(downPos).getBlock() instanceof DownVerticalConveyorBlock && world.getBlockState(downPos).get(FACING) == direction)
            conveyorBlockEntity.setDown(true);
        else
            conveyorBlockEntity.setDown(false);

        world.setBlockState(blockPos, newState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction fromDirection, BlockState fromState, IWorld world, BlockPos blockPos, BlockPos fromPos) {
        BlockState newState = blockState;
        Direction direction = newState.get(FACING);

        BlockPos frontPos = blockPos.offset(direction.getOpposite());
        BlockPos conveyorPos = blockPos.offset(direction).up();

        if (world.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world.getBlockState(frontPos).get(FACING) == direction.getOpposite())
            newState = newState.with(ConveyorProperties.FRONT, true);
        else
            newState = newState.with(ConveyorProperties.FRONT, false);

        if (world.getBlockState(blockPos.up()).isAir() && world.getBlockState(conveyorPos).getBlock() instanceof ConveyorBlock && world.getBlockState(conveyorPos).get(FACING) == direction.getOpposite())
            newState = newState.with(ConveyorProperties.CONVEYOR, true);
        else
            newState = newState.with(ConveyorProperties.CONVEYOR, false);

        return newState;
    }
}
