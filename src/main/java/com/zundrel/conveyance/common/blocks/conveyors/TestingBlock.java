package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.api.ConveyableBlock;
import com.zundrel.conveyance.common.blocks.entities.TestBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TestingBlock extends HorizontalFacingBlock implements BlockEntityProvider, ConveyableBlock {
    public TestingBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new TestBlockEntity();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManagerBuilder) {
        stateManagerBuilder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayer().isSneaking() ? itemPlacementContext.getPlayerFacing().getOpposite() : itemPlacementContext.getPlayerFacing());
    }

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		updateDiagonals(world, this, blockPos);
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof TestBlockEntity) {
				((TestBlockEntity) blockEntity).setRemoved(true);
			}

			super.onBlockRemoved(state, world, pos, newState, notify);
		}

		updateDiagonals(world, this, pos);
	}
}
