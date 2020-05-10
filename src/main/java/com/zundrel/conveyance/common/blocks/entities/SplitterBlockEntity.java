package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;

public class SplitterBlockEntity extends DoubleMachineBlockEntity {
	public SplitterBlockEntity() {
        super(ConveyanceBlockEntities.SPLITTER);
    }

    public SplitterBlockEntity(BlockEntityType type) {
        super(type);
    }

	@Override
	public void give(ItemStack stack) {
		int size = stack.getCount();
		int smallHalf = size / 2;
		int largeHalf = size - smallHalf;

		ItemStack smallStack = stack.copy();
		ItemStack largeStack = stack.copy();

		smallStack.setCount(smallHalf);
		largeStack.setCount(largeHalf);

		if (smallStack.getCount() > 0)
			setLeftStack(smallStack);

		if (largeStack.getCount() > 0)
			setRightStack(largeStack);
	}
}
