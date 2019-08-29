package com.zundrel.conveyance.common.inventory;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.minecraft.item.ItemStack;

public class ConveyorExtractable implements ItemExtractable {
	protected final ConveyorBlockEntity conveyor;
	
	public ConveyorExtractable(ConveyorBlockEntity conveyor) {
		this.conveyor = conveyor;
	}
	
	@Override
	public ItemStack attemptExtraction(ItemFilter filter, int maxAmount, Simulation simulation) {
		if (!conveyor.isEmpty()) {
			if (filter.matches(conveyor.getStack())) {
				if (maxAmount > conveyor.getStack().getCount()) {
					ItemStack extracted = conveyor.getStack().copy();
					extracted.setCount(maxAmount);
					
					conveyor.getStack().decrement(maxAmount);
					conveyor.markDirty();
					
					return extracted;
				} else {
					ItemStack extracted = conveyor.getStack();
					
					conveyor.removeStack();
					conveyor.markDirty();
					
					return extracted;
				}
			}
		}
		
		return ItemStack.EMPTY;
	}

}