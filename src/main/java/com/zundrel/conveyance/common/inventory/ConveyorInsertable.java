package com.zundrel.conveyance.common.inventory;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.minecraft.item.ItemStack;

public class ConveyorInsertable implements ItemInsertable {
	protected final ConveyorBlockEntity delegate;
	
	public ConveyorInsertable(ConveyorBlockEntity delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
		if (delegate.getStack().isEmpty()) {
			if (simulation==Simulation.ACTION) {
				delegate.setStack(stack.copy());
				delegate.setPosition(0);
			}
			return ItemStack.EMPTY;
		} else {
			return stack;
		}
	}

}