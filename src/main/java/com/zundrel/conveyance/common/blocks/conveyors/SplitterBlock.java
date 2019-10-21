package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.api.IConveyorMachine;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SplitterBlock extends HorizontalFacingBlock implements IConveyorMachine {
    public SplitterBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(ConveyorProperties.CONVEYOR, false));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos fromPos, boolean boolean_1) {
        BlockState newState = blockState;
        Direction direction = newState.get(FACING);

        BlockPos leftPos = blockPos.offset(direction.rotateYCounterclockwise());
        BlockPos rightPos = blockPos.offset(direction.rotateYClockwise());

        if (world.getBlockState(leftPos).getBlock() instanceof ConveyorBlock && world.getBlockState(rightPos).getBlock() instanceof ConveyorBlock)
            newState = newState.with(ConveyorProperties.CONVEYOR, true);
        else
            newState = newState.with(ConveyorProperties.CONVEYOR, false);

        world.setBlockState(blockPos, newState);

        super.neighborUpdate(blockState, world, blockPos, block, fromPos, boolean_1);
    }

    @Override
    public void insert(World world, BlockPos pos, BlockState state, ConveyorBlockEntity blockEntity, ItemStack stack, Direction direction) {
        if (state.get(ConveyorProperties.CONVEYOR)) {
            Direction facing = state.get(FACING);
            BlockPos leftPos = pos.offset(facing.rotateYCounterclockwise());
            BlockPos rightPos = pos.offset(facing.rotateYClockwise());
            int size = stack.getCount();
            int smallHalf = size / 2;
            int largeHalf = size - smallHalf;

            ConveyorBlockEntity conveyorOne = (ConveyorBlockEntity) world.getBlockEntity(leftPos);
            ConveyorBlockEntity conveyorTwo = (ConveyorBlockEntity) world.getBlockEntity(rightPos);

            ItemStack smallStack = stack.copy();
            ItemStack largeStack = stack.copy();

            smallStack.setCount(smallHalf);
            largeStack.setCount(largeHalf);

            conveyorOne.setStack(smallStack);
            conveyorTwo.setStack(largeStack);

            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.1F, 1);
        }
    }

    @Override
    public boolean canInsert(World world, BlockPos pos, BlockState state, ConveyorBlockEntity blockEntity, ItemStack stack, Direction direction) {
        Direction facing = state.get(FACING);
        ConveyorBlockEntity conveyorOne = (ConveyorBlockEntity) world.getBlockEntity(pos.offset(facing.rotateYCounterclockwise()));
        ConveyorBlockEntity conveyorTwo = (ConveyorBlockEntity) world.getBlockEntity(pos.offset(facing.rotateYClockwise()));

        if (state.get(ConveyorProperties.CONVEYOR) && conveyorOne != null && conveyorTwo != null) {
            return direction == facing.getOpposite() && conveyorOne.isEmpty() && conveyorTwo.isEmpty();
        }

        return false;
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactoryBuilder) {
        stateFactoryBuilder.add(new Property[]{FACING, ConveyorProperties.CONVEYOR});
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing());
    }
}
