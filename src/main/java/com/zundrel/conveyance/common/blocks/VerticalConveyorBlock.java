package com.zundrel.conveyance.common.blocks;

import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.common.utilities.RotationUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VerticalConveyorBlock extends HorizontalFacingBlock implements IConveyor {
    public VerticalConveyorBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(ConveyorProperties.FRONT, false));
    }

    @Override
    public void onWrenched(PlayerEntity player) {

    }

    @Override
    public void onBlockAdded(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1) {
        world_1.updateNeighbor(blockPos_1, this, blockPos_1);
    }

    @Override
    public void neighborUpdate(BlockState blockState_1, World world_1, BlockPos blockPos_1, Block block_1, BlockPos blockPos_2, boolean boolean_1) {
        BlockState newState = blockState_1;
        Direction direction = newState.get(FACING);

        BlockPos frontPos = blockPos_1.offset(direction.getOpposite());
        BlockPos upPos = blockPos_1.up();

        if (world_1.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world_1.getBlockState(frontPos).get(FACING) == direction)
            newState = newState.with(ConveyorProperties.FRONT, true);
        else
            newState = newState.with(ConveyorProperties.FRONT, false);

        world_1.setBlockState(blockPos_1, newState);

        super.neighborUpdate(blockState_1, world_1, blockPos_1, block_1, blockPos_2, boolean_1);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactoryBuilder) {
        stateFactoryBuilder.add(new Property[]{FACING, ConveyorProperties.DOWN, ConveyorProperties.FRONT, ConveyorProperties.UP});
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
