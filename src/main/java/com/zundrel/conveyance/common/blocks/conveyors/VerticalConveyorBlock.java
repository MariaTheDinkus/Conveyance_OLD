package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.utilities.RotationUtilities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VerticalConveyorBlock extends HorizontalFacingBlock implements BlockEntityProvider, IConveyor {
    public VerticalConveyorBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(ConveyorProperties.FRONT, false).with(ConveyorProperties.CONVEYOR, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new VerticalConveyorBlockEntity();
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        if (!playerEntity.getStackInHand(hand).isEmpty() && Block.getBlockFromItem(playerEntity.getStackInHand(hand).getItem()) instanceof IConveyor)
            return false;

        if (!playerEntity.getStackInHand(hand).isEmpty() && blockEntity.isEmpty()) {
            blockEntity.setStack(playerEntity.getStackInHand(hand));
            playerEntity.setStackInHand(hand, ItemStack.EMPTY);

            return true;
        } else if (!blockEntity.isEmpty()) {
            playerEntity.inventory.offerOrDrop(world, blockEntity.getStack());
            blockEntity.removeStack();

            return true;
        }

        return false;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        world.updateNeighbor(blockPos, this, blockPos);
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        if (blockState.getBlock() != blockState2.getBlock()) {
            BlockEntity blockEntity_1 = world.getBlockEntity(blockPos);
            if (blockEntity_1 instanceof VerticalConveyorBlockEntity) {
                ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((VerticalConveyorBlockEntity) blockEntity_1).getStack());
                world.updateHorizontalAdjacent(blockPos, this);
            }

            super.onBlockRemoved(blockState, world, blockPos, blockState2, boolean_1);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos fromPos, boolean boolean_1) {
        BlockState newState = blockState;
        Direction direction = newState.get(FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);
        boolean up = false;

        BlockPos frontPos = blockPos.offset(direction.getOpposite());
        BlockPos upPos = blockPos.up();
        BlockPos conveyorPos = blockPos.offset(direction).up();

        if (world.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world.getBlockState(frontPos).get(FACING) == direction) {
            newState = newState.with(ConveyorProperties.FRONT, true);
            up = true;
        } else
            newState = newState.with(ConveyorProperties.FRONT, false);

        if (world.getBlockState(upPos).getBlock() instanceof VerticalConveyorBlock && !world.getBlockState(upPos).get(ConveyorProperties.FRONT) && world.getBlockState(upPos).get(FACING) == direction)
            ((VerticalConveyorBlockEntity) conveyorBlockEntity).setUp(true);
        else
            ((VerticalConveyorBlockEntity) conveyorBlockEntity).setUp(false);

        if (!up && world.getBlockState(upPos).isAir() && world.getBlockState(conveyorPos).getBlock() instanceof ConveyorBlock && world.getBlockState(conveyorPos).get(FACING) == direction)
            newState = newState.with(ConveyorProperties.CONVEYOR, true);
        else
            newState = newState.with(ConveyorProperties.CONVEYOR, false);

        world.setBlockState(blockPos, newState);

        super.neighborUpdate(blockState, world, blockPos, block, fromPos, boolean_1);
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        return ((ConveyorBlockEntity) world.getBlockEntity(blockPos)).isEmpty() ? 0 : 15;
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactoryBuilder) {
        stateFactoryBuilder.add(new Property[]{FACING, ConveyorProperties.FRONT, ConveyorProperties.CONVEYOR});
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing());
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaque(BlockState blockState) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        VoxelShape box1 = RotationUtilities.getRotatedShape(new Box(0, 0, 0, 1, 1, (4F / 16F)), blockState.get(FACING));
        VoxelShape box2 = RotationUtilities.getRotatedShape(new Box(0, 0, 0, 1, (4F / 16F), 1), blockState.get(FACING));

        if (blockState.get(ConveyorProperties.FRONT)) {
            return VoxelShapes.union(box1, box2);
        } else {
            return box1;
        }
    }
}
