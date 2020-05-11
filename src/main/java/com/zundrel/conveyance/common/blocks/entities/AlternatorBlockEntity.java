package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;

public class AlternatorBlockEntity extends DoubleMachineBlockEntity {
	public boolean right = false;

	public AlternatorBlockEntity() {
        super(ConveyanceBlockEntities.ALTERNATOR);
    }

    public AlternatorBlockEntity(BlockEntityType type) {
        super(type);
    }

	@Override
	public void give(ItemStack stack) {
		ItemStack copyStack = stack.copy();

		if (right) {
			setRightStack(copyStack);
		} else {
			setLeftStack(copyStack);
		}
		right = !right;
	}
}
