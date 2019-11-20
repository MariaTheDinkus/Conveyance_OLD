package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.api.ConveyorType;
import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.api.IConveyorMachine;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.items.WrenchItem;
import com.zundrel.conveyance.common.utilities.MovementUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConveyorBlock extends HorizontalFacingBlock implements BlockEntityProvider, IConveyor {
    private int speed;

    public ConveyorBlock(Settings settings, int speed) {
        super(settings);

        this.speed = speed;
        setDefaultState(getDefaultState().with(ConveyorProperties.LEFT, false).with(ConveyorProperties.RIGHT, false).with(ConveyorProperties.UP, false));
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public ConveyorType getType() {
        return ConveyorType.NORMAL;
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
        if (!entity.onGround || (entity.getY() - blockPos.getY()) != (4F / 16F))
            return;

        if (entity instanceof PlayerEntity && entity.isSneaking())
            return;

        Direction direction = blockState.get(FACING);

        if (entity instanceof ItemEntity && entity.getBlockPos().equals(blockPos) && world.getBlockEntity(blockPos) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

            if (blockEntity.isEmpty()) {
                blockEntity.setStack(((ItemEntity) entity).getStack());
                entity.remove();
            }
        } else if (!(entity instanceof ItemEntity)) {
            MovementUtilities.pushEntity(entity, blockPos, 2.0F / getSpeed(), direction);
        }
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        Direction facing = blockState.get(FACING);

        world.updateNeighbors(blockPos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighbor(blockPos.offset(direction).down(1), this, blockPos);
        }
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        Direction facing = blockState.get(FACING);
        if (blockState.getBlock() != blockState2.getBlock()) {
            BlockEntity blockEntity_1 = world.getBlockEntity(blockPos);
            if (blockEntity_1 instanceof ConveyorBlockEntity) {
                ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((ConveyorBlockEntity) blockEntity_1).getStack());
            }

            world.updateNeighbors(blockPos, this);
            for (Direction direction : Direction.values()) {
                world.updateNeighbor(blockPos.offset(direction).down(1), this, blockPos);
            }

            super.onBlockRemoved(blockState, world, blockPos, blockState2, boolean_1);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction fromDirection, BlockState fromState, IWorld world, BlockPos blockPos, BlockPos fromPos) {
        BlockState newState = blockState;
        Direction direction = newState.get(FACING);

        BlockPos leftPos = blockPos.offset(direction.rotateYCounterclockwise());
        BlockPos rightPos = blockPos.offset(direction.rotateYClockwise());
        BlockPos upPos = blockPos.up();

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

        return newState;
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
        BlockState newState = blockState.getStateForNeighborUpdate(null, blockState, world, blockPos, blockPos2);
        Direction direction = newState.get(FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        BlockPos frontPos = blockPos.offset(direction);

        if (world.getBlockEntity(blockPos.offset(direction)) instanceof ConveyorBlockEntity)
            conveyorBlockEntity.setFront(true);
        else
            conveyorBlockEntity.setFront(false);

        if (world.getBlockState(blockPos.offset(direction)).getBlock() instanceof IConveyorMachine)
            conveyorBlockEntity.setFront(true);
        else if (!conveyorBlockEntity.hasFront())
            conveyorBlockEntity.setFront(false);

        if (world.getBlockEntity(blockPos.offset(direction).down(1)) instanceof DownVerticalConveyorBlockEntity)
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

        world.setBlockState(blockPos, newState);
    }

    @Override
    public boolean onBlockAction(BlockState blockState_1, World world_1, BlockPos blockPos_1, int int_1, int int_2) {
        return super.onBlockAction(blockState_1, world_1, blockPos_1, int_1, int_2);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        if (!playerEntity.getStackInHand(hand).isEmpty() && (Block.getBlockFromItem(playerEntity.getStackInHand(hand).getItem()) instanceof IConveyor || playerEntity.getStackInHand(hand).getItem() instanceof WrenchItem)) {
            return ActionResult.PASS;
        } else if (!playerEntity.getStackInHand(hand).isEmpty() && blockEntity.isEmpty()) {
            blockEntity.setStack(playerEntity.getStackInHand(hand));
            playerEntity.setStackInHand(hand, ItemStack.EMPTY);

            return ActionResult.SUCCESS;
        } else if (!blockEntity.isEmpty()) {
            playerEntity.inventory.offerOrDrop(world, blockEntity.getStack());
            blockEntity.removeStack();

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManagerBuilder) {
        stateManagerBuilder.add(new Property[]{FACING, ConveyorProperties.LEFT, ConveyorProperties.RIGHT, ConveyorProperties.UP});
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing());
    }

    @Override
    public boolean isTranslucent(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return true;
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
