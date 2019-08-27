package com.zundrel.conveyance.common.blocks;

import com.zundrel.conveyance.api.Casing;
import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.items.WrenchItem;
import com.zundrel.conveyance.common.registries.ModBlocks;
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

        setDefaultState(getDefaultState().with(ConveyorProperties.CASING, Casing.NONE).with(ConveyorProperties.LEFT, false).with(ConveyorProperties.RIGHT, false).with(ConveyorProperties.UP, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new ConveyorBlockEntity();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onWrenched(World world, BlockState state, BlockPos pos, PlayerEntity player) {
        if (!player.isSneaking())
            world.setBlockState(pos, state.with(FACING, state.get(FACING).rotateYClockwise()));
        else if (state.get(ConveyorProperties.CASING) == Casing.GLASS) {
            world.setBlockState(pos, state.with(ConveyorProperties.CASING, Casing.NONE));
            if (!player.isCreative())
                player.inventory.offerOrDrop(world, new ItemStack(Blocks.GLASS));
        } else if (state.get(ConveyorProperties.CASING) == Casing.OPAQUE) {
            world.setBlockState(pos, state.with(ConveyorProperties.CASING, Casing.NONE));
            if (!player.isCreative())
                player.inventory.offerOrDrop(world, new ItemStack(Blocks.IRON_BLOCK));
        }
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
    public void onEntityCollision(BlockState blockState_1, World world_1, BlockPos blockPos_1, Entity entity_1) {
        if (!entity_1.onGround)
            return;

        if (entity_1 instanceof ItemEntity || entity_1 instanceof PlayerEntity && entity_1.isSneaking())
            return;

        Direction direction = blockState_1.get(FACING);

        MovementUtilities.pushEntity(entity_1, blockPos_1, 0.125F, direction);
    }

    @Override
    public boolean activate(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
        ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world_1.getBlockEntity(blockPos_1);

        if (!playerEntity_1.getStackInHand(hand_1).isEmpty() && (Block.getBlockFromItem(playerEntity_1.getStackInHand(hand_1).getItem()) instanceof IConveyor || playerEntity_1.getStackInHand(hand_1).getItem() instanceof WrenchItem))
            return false;

        if (blockState_1.get(ConveyorProperties.CASING) == Casing.NONE && !playerEntity_1.getStackInHand(hand_1).isEmpty()) {
            if (Block.getBlockFromItem(playerEntity_1.getStackInHand(hand_1).getItem()) == Blocks.GLASS) {
                world_1.setBlockState(blockPos_1, blockState_1.with(ConveyorProperties.CASING, Casing.GLASS));
                if (!playerEntity_1.isCreative())
                    playerEntity_1.getStackInHand(hand_1).decrement(1);

                return true;
            } else if (Block.getBlockFromItem(playerEntity_1.getStackInHand(hand_1).getItem()) == Blocks.IRON_BLOCK) {
                world_1.setBlockState(blockPos_1, blockState_1.with(ConveyorProperties.CASING, Casing.OPAQUE));
                if (!playerEntity_1.isCreative())
                    playerEntity_1.getStackInHand(hand_1).decrement(1);

                return true;
            }
        }

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

        world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING)).down(), this, blockPos_1);
        world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING).getOpposite()).down(), this, blockPos_1);
    }

    @Override
    public void onBlockRemoved(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1) {
        if (blockState_1.getBlock() != blockState_2.getBlock()) {
            BlockEntity blockEntity_1 = world_1.getBlockEntity(blockPos_1);
            if (blockEntity_1 instanceof ConveyorBlockEntity) {
                ItemScatterer.spawn(world_1, blockPos_1, (ConveyorBlockEntity) blockEntity_1);
                world_1.updateHorizontalAdjacent(blockPos_1, this);
            }

            world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING)).down(), this, blockPos_1);
            world_1.updateNeighbor(blockPos_1.offset(blockState_1.get(FACING).getOpposite()).down(), this, blockPos_1);

            super.onBlockRemoved(blockState_1, world_1, blockPos_1, blockState_2, boolean_1);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState_1, World world_1, BlockPos blockPos_1, Block block_1, BlockPos blockPos_2, boolean boolean_1) {
        BlockState newState = blockState_1;
        Direction direction = newState.get(FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world_1.getBlockEntity(blockPos_1);

        BlockPos leftPos = blockPos_1.offset(direction.rotateYCounterclockwise());
        BlockPos rightPos = blockPos_1.offset(direction.rotateYClockwise());
        BlockPos upPos = blockPos_1.up();

        if (world_1.getBlockEntity(blockPos_1.offset(direction)) instanceof ConveyorBlockEntity)
            conveyorBlockEntity.setFront(true);
        else
            conveyorBlockEntity.setFront(false);

        if (world_1.getBlockState(leftPos).getBlock() instanceof ConveyorBlock && world_1.getBlockState(leftPos).get(FACING) == direction.rotateYClockwise())
            newState = newState.with(ConveyorProperties.LEFT, true);
        else
            newState = newState.with(ConveyorProperties.LEFT, false);

        if (world_1.getBlockState(rightPos).getBlock() instanceof ConveyorBlock && world_1.getBlockState(rightPos).get(FACING) == direction.rotateYCounterclockwise())
            newState = newState.with(ConveyorProperties.RIGHT, true);
        else
            newState = newState.with(ConveyorProperties.RIGHT, false);

        if (newState.get(ConveyorProperties.CASING) != Casing.NONE || world_1.getBlockState(upPos).getBlock() instanceof ConveyorBlock || world_1.getBlockState(upPos).getBlock() instanceof VerticalConveyorBlock && world_1.getBlockState(upPos).get(ConveyorProperties.FRONT))
            newState = newState.with(ConveyorProperties.UP, true);
        else
            newState = newState.with(ConveyorProperties.UP, false);

        world_1.setBlockState(blockPos_1, newState);

        super.neighborUpdate(blockState_1, world_1, blockPos_1, block_1, blockPos_2, boolean_1);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactoryBuilder) {
        stateFactoryBuilder.add(new Property[]{FACING, ConveyorProperties.CASING, ConveyorProperties.LEFT, ConveyorProperties.RIGHT, ConveyorProperties.UP});
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext_1) {
        return this.getDefaultState().with(FACING, itemPlacementContext_1.getPlayer().isSneaking() ? itemPlacementContext_1.getPlayerFacing().getOpposite() : itemPlacementContext_1.getPlayerFacing());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, EntityContext entityContext_1) {
        VoxelShape conveyor = VoxelShapes.cuboid(0, 0, 0, 1, (4F / 16F), 1);
        if (blockState_1.get(ConveyorProperties.UP)) {
            return VoxelShapes.fullCube();
        }

        return conveyor;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, EntityContext entityContext_1) {
        VoxelShape conveyor = VoxelShapes.cuboid(0, 0, 0, 1, (4F / 16F), 1);
        if (blockState_1.get(ConveyorProperties.UP)) {
            return VoxelShapes.fullCube();
        }

        return conveyor;
    }

    @Override
    public boolean isOpaque(BlockState blockState_1) {
        return false;
    }
}
