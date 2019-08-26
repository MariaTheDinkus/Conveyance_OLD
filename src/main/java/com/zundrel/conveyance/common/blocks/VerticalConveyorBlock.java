package com.zundrel.conveyance.common.blocks;

import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.utilities.RotationUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.BooleanBiFunction;
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

        setDefaultState(getDefaultState().with(ConveyorProperties.FRONT, false).with(ConveyorProperties.UP, false).with(ConveyorProperties.CONVEYOR, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new VerticalConveyorBlockEntity();
    }

    @Override
    public void onWrenched(PlayerEntity player) {

    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState_1) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState_1, World world_1, BlockPos blockPos_1) {
        return ((ConveyorBlockEntity) world_1.getBlockEntity(blockPos_1)).isInvEmpty() ? 0 : 15;
    }

    @Override
    public boolean activate(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
        VerticalConveyorBlockEntity blockEntity = (VerticalConveyorBlockEntity) world_1.getBlockEntity(blockPos_1);

        if (!playerEntity_1.getStackInHand(hand_1).isEmpty() && Block.getBlockFromItem(playerEntity_1.getStackInHand(hand_1).getItem()) instanceof IConveyor)
            return false;

        if (!playerEntity_1.getStackInHand(hand_1).isEmpty() && blockEntity.getInvStack(0).isEmpty()) {
            blockEntity.setInvStack(0, playerEntity_1.getStackInHand(hand_1));
            playerEntity_1.setStackInHand(hand_1, ItemStack.EMPTY);

            return true;
        } else if (!blockEntity.getInvStack(0).isEmpty()) {
            playerEntity_1.inventory.offerOrDrop(world_1, blockEntity.getInvStack(0));
            blockEntity.removeInvStack(0);

            return true;
        }

        return false;
    }

    @Override
    public void onBlockAdded(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1) {
        world_1.updateNeighbor(blockPos_1, this, blockPos_1);
    }

    @Override
    public void onBlockRemoved(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1) {
        if (blockState_1.getBlock() != blockState_2.getBlock()) {
            BlockEntity blockEntity_1 = world_1.getBlockEntity(blockPos_1);
            if (blockEntity_1 instanceof VerticalConveyorBlockEntity) {
                ItemScatterer.spawn(world_1, blockPos_1, (VerticalConveyorBlockEntity) blockEntity_1);
                world_1.updateHorizontalAdjacent(blockPos_1, this);
            }

            super.onBlockRemoved(blockState_1, world_1, blockPos_1, blockState_2, boolean_1);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState_1, World world_1, BlockPos blockPos_1, Block block_1, BlockPos blockPos_2, boolean boolean_1) {
        BlockState newState = blockState_1;
        Direction direction = newState.get(FACING);

        BlockPos frontPos = blockPos_1.offset(direction.getOpposite());
        BlockPos upPos = blockPos_1.up();
        BlockPos conveyorPos = blockPos_1.offset(direction).up();

        if (world_1.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world_1.getBlockState(frontPos).get(FACING) == direction)
            newState = newState.with(ConveyorProperties.FRONT, true);
        else
            newState = newState.with(ConveyorProperties.FRONT, false);

        if (world_1.getBlockState(upPos).getBlock() instanceof VerticalConveyorBlock)
            newState = newState.with(ConveyorProperties.UP, true);
        else
            newState = newState.with(ConveyorProperties.UP, false);

        if (!newState.get(ConveyorProperties.UP) && world_1.isAir(upPos) && world_1.getBlockState(conveyorPos).getBlock() instanceof ConveyorBlock && world_1.getBlockState(conveyorPos).get(FACING) == direction)
            newState = newState.with(ConveyorProperties.CONVEYOR, true);
        else
            newState = newState.with(ConveyorProperties.CONVEYOR, false);

        world_1.setBlockState(blockPos_1, newState);

        super.neighborUpdate(blockState_1, world_1, blockPos_1, block_1, blockPos_2, boolean_1);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactoryBuilder) {
        stateFactoryBuilder.add(new Property[]{FACING, ConveyorProperties.FRONT, ConveyorProperties.UP, ConveyorProperties.CONVEYOR});
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext_1) {
        return this.getDefaultState().with(FACING, itemPlacementContext_1.getPlayerFacing());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, EntityContext entityContext_1) {
        Box box1 = RotationUtilities.getRotatedBoundingBox(new Box(0, 0, 0, 1, (4F / 16F), 1), blockState_1.get(FACING).getOpposite());
        Box box2 = RotationUtilities.getRotatedBoundingBox(new Box(0, 0, 0, 1, 1, (4F / 16F)), blockState_1.get(FACING).getOpposite());

        if (blockState_1.get(ConveyorProperties.FRONT)) {
            return VoxelShapes.union(VoxelShapes.cuboid(box1), VoxelShapes.cuboid(box2));
        } else {
            return VoxelShapes.cuboid(box2);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, EntityContext entityContext_1) {
        Box box1 = RotationUtilities.getRotatedBoundingBox(new Box(0, 0, 0, 1, (4F / 16F), 1), blockState_1.get(FACING).getOpposite());
        Box box2 = RotationUtilities.getRotatedBoundingBox(new Box(0, 0, 0, 1, 1, (4F / 16F)), blockState_1.get(FACING).getOpposite());

        if (blockState_1.get(ConveyorProperties.FRONT)) {
            return VoxelShapes.union(VoxelShapes.cuboid(box1), VoxelShapes.cuboid(box2));
        } else {
            return VoxelShapes.cuboid(box2);
        }
    }

    @Override
    public boolean isOpaque(BlockState blockState_1) {
        return false;
    }
}
