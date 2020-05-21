package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.api.Conveyable;
import com.zundrel.conveyance.api.ConveyorType;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;

public class IncineratorBlockEntity extends BlockEntity implements Conveyable {
	public boolean hasBeenRemoved = false;

	public IncineratorBlockEntity() {
        super(ConveyanceBlockEntities.INCINERATOR);
    }

    public IncineratorBlockEntity(BlockEntityType type) {
        super(type);
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
		return true;
	}

	@Override
	public boolean validInputSide(Direction direction) {
		return direction == getCachedState().get(HorizontalFacingBlock.FACING).getOpposite();
	}

	@Override
	public boolean isOutputSide(Direction direction, ConveyorType type) {
		return false;
	}

	@Override
	public void give(ItemStack stack) {
		getWorld().playSound(null, getPos().getX(), getPos().getY(), getPos().getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.125F, 1.0F);
	}
}
