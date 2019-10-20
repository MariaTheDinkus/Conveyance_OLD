package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.api.IConveyorMachine;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.items.WrenchItem;
import com.zundrel.conveyance.common.utilities.MovementUtilities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ConveyorBlock extends HorizontalFacingBlock implements BlockEntityProvider, IConveyor {
    public ConveyorBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(ConveyorProperties.LEFT, false).with(ConveyorProperties.RIGHT, false).with(ConveyorProperties.UP, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new ConveyorBlockEntity();
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
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (!entity.onGround || (entity.y - blockPos.getY()) != (4F / 16F))
            return;

        if (entity instanceof ItemEntity || entity instanceof PlayerEntity && entity.isSneaking())
            return;

        Direction direction = blockState.get(FACING);

        MovementUtilities.pushEntity(entity, blockPos, 0.125F, direction);
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        world.updateNeighbor(blockPos, this, blockPos);

        world.updateNeighbor(blockPos.offset(blockState.get(FACING)).down(), this, blockPos);
        world.updateNeighbor(blockPos.offset(blockState.get(FACING).getOpposite()).down(), this, blockPos);
        world.updateNeighbor(blockPos.offset(blockState.get(FACING)).offset(blockState.get(FACING)), this, blockPos);
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        if (blockState.getBlock() != blockState2.getBlock()) {
            BlockEntity blockEntity_1 = world.getBlockEntity(blockPos);
            if (blockEntity_1 instanceof ConveyorBlockEntity) {
                ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((ConveyorBlockEntity) blockEntity_1).getStack());
                world.updateHorizontalAdjacent(blockPos, this);
            }

            world.updateNeighbor(blockPos.offset(blockState.get(FACING)).down(), this, blockPos);
            world.updateNeighbor(blockPos.offset(blockState.get(FACING).getOpposite()).down(), this, blockPos);
            world.updateNeighbor(blockPos.offset(blockState.get(FACING)).offset(blockState.get(FACING)), this, blockPos);

            super.onBlockRemoved(blockState, world, blockPos, blockState2, boolean_1);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos fromPos, boolean boolean_1) {
        BlockState newState = blockState;
        Direction direction = newState.get(FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        BlockPos frontPos = blockPos.offset(direction);
        BlockPos leftPos = blockPos.offset(direction.rotateYCounterclockwise());
        BlockPos rightPos = blockPos.offset(direction.rotateYClockwise());
        BlockPos upPos = blockPos.up();

        if (world.getBlockEntity(blockPos.offset(direction)) instanceof ConveyorBlockEntity)
            conveyorBlockEntity.setFront(true);
        else
            conveyorBlockEntity.setFront(false);

        if (world.getBlockState(blockPos.offset(direction)).getBlock() instanceof IConveyorMachine)
            conveyorBlockEntity.setFront(true);
        else if (!conveyorBlockEntity.hasFront())
            conveyorBlockEntity.setFront(false);

        if (world.getBlockEntity(blockPos.offset(direction).down()) instanceof DownVerticalConveyorBlockEntity)
            conveyorBlockEntity.setDown(true);
        else
            conveyorBlockEntity.setDown(false);

        if (!conveyorBlockEntity.hasDown() && world.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world.getBlockState(frontPos.offset(direction)).getBlock() instanceof ConveyorBlock){
            Direction frontDirection = world.getBlockState(frontPos).get(FACING);
            Direction acrossDirection = world.getBlockState(frontPos.offset(direction)).get(FACING);
            if (acrossDirection == direction.getOpposite() && (frontDirection == direction.rotateYClockwise() || frontDirection == direction.rotateYCounterclockwise())) {
                conveyorBlockEntity.setAcross(true);
            } else
                conveyorBlockEntity.setAcross(false);
        } else
            conveyorBlockEntity.setAcross(false);

        if (world.getBlockState(leftPos).getBlock() instanceof ConveyorBlock && world.getBlockState(leftPos).get(FACING) == direction.rotateYClockwise())
            newState = newState.with(ConveyorProperties.LEFT, true);
        else if (world.getBlockState(leftPos).getBlock() instanceof IConveyorMachine)
            newState = newState.with(ConveyorProperties.LEFT, true);
        else
            newState = newState.with(ConveyorProperties.LEFT, false);

        if (world.getBlockState(rightPos).getBlock() instanceof ConveyorBlock && world.getBlockState(rightPos).get(FACING) == direction.rotateYCounterclockwise())
            newState = newState.with(ConveyorProperties.RIGHT, true);
        else if (world.getBlockState(rightPos).getBlock() instanceof IConveyorMachine)
            newState = newState.with(ConveyorProperties.RIGHT, true);
        else
            newState = newState.with(ConveyorProperties.RIGHT, false);

        if (world.getBlockState(upPos).getBlock() instanceof ConveyorBlock || world.getBlockState(upPos).getBlock() instanceof VerticalConveyorBlock && world.getBlockState(upPos).get(ConveyorProperties.FRONT))
            newState = newState.with(ConveyorProperties.UP, true);
        else
            newState = newState.with(ConveyorProperties.UP, false);

        world.setBlockState(blockPos, newState);

        super.neighborUpdate(blockState, world, blockPos, block, fromPos, boolean_1);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        if (!playerEntity.getStackInHand(hand).isEmpty() && (Block.getBlockFromItem(playerEntity.getStackInHand(hand).getItem()) instanceof IConveyor || playerEntity.getStackInHand(hand).getItem() instanceof WrenchItem)) {
            return false;
        } else if (!playerEntity.getStackInHand(hand).isEmpty() && blockEntity.isEmpty()) {
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
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactoryBuilder) {
        stateFactoryBuilder.add(new Property[]{FACING, ConveyorProperties.LEFT, ConveyorProperties.RIGHT, ConveyorProperties.UP});
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing());
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
        VoxelShape conveyor = VoxelShapes.cuboid(0, 0, 0, 1, (4F / 16F), 1);
        if (blockState.get(ConveyorProperties.UP)) {
            return VoxelShapes.fullCube();
        }
        return conveyor;
    }
}
