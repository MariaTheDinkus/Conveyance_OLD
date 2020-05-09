package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.api.Conveyable;
import com.zundrel.conveyance.api.ConveyorType;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class TestBlockEntity extends BlockEntity implements Conveyable, Tickable {
	boolean hasBeenRemoved = false;

	public TestBlockEntity() {
        super(ConveyanceBlockEntities.TEST);
    }

    public TestBlockEntity(BlockEntityType type) {
        super(type);
    }

	@Override
    public void tick() {

    }

	@Override
	public boolean hasBeenRemoved() {
		return hasBeenRemoved;
	}

	@Override
	public void setRemoved(boolean hasBeenRemoved) {
		this.hasBeenRemoved = hasBeenRemoved;
	}

	@Override
	public boolean accepts(ItemStack stack) {
		return false;
	}

	@Override
	public boolean validInputSide(Direction direction) {
		return true;
	}

	@Override
	public Direction getOutputSide(ConveyorType type) {
		return getCachedState().get(HorizontalFacingBlock.FACING);
	}

	@Override
	public void give(ItemStack stack) {

	}
}
