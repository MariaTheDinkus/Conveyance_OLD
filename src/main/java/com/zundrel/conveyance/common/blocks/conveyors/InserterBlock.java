package com.zundrel.conveyance.common.blocks.conveyors;

import com.sun.java.accessibility.util.internal.TextComponentTranslator;
import com.zundrel.conveyance.api.ConveyableBlock;
import com.zundrel.conveyance.common.blocks.entities.InserterBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.logging.LogManager;

public class InserterBlock extends HorizontalFacingBlock implements BlockEntityProvider, ConveyableBlock {
    private String type;
    private int speed;

    public InserterBlock(String type, int speed, Settings settings) {
        super(settings);

        this.type = type;
        this.speed = speed;
    }

    public String getType() {
        return type;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new InserterBlockEntity();
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
            if (blockEntity instanceof InserterBlockEntity) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ((InserterBlockEntity) blockEntity).getStack());
            }

            super.onBlockRemoved(state, world, pos, newState, notify);
        }

        updateDiagonals(world, this, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1);
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
        Direction direction = blockState.get(FACING);
        InserterBlockEntity machineBlockEntity = (InserterBlockEntity) world.getBlockEntity(blockPos);

        BlockPos frontPos = blockPos.offset(direction);
        BlockPos behindPos = blockPos.offset(direction.getOpposite());

        BlockEntity frontBlockEntity = world.getBlockEntity(frontPos);
        if (frontBlockEntity instanceof Inventory && !(frontBlockEntity instanceof InserterBlockEntity))
            machineBlockEntity.setHasOutput(true);
        else
            machineBlockEntity.setHasOutput(false);

        BlockEntity behindBlockEntity = world.getBlockEntity(behindPos);
        if (behindBlockEntity instanceof Inventory && !(frontBlockEntity instanceof InserterBlockEntity))
            machineBlockEntity.setHasInput(true);
        else
            machineBlockEntity.setHasInput(false);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        InserterBlockEntity inserter = (InserterBlockEntity) world.getBlockEntity(blockPos);
        playerEntity.playSound(SoundEvents.BLOCK_LEVER_CLICK, 1.0f, 1.0f);


            if (playerEntity.getStackInHand(hand).isEmpty()) {
                // Player is sneaking, no item, reset filter
                inserter.clearFilterItem();
                return ActionResult.SUCCESS;
            } else {
                inserter.setFilterItem(playerEntity.getStackInHand(hand).getItem());
                world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_BELL_USE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }


    }
}
