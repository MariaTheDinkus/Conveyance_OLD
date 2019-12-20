package com.zundrel.conveyance.common.inventory;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.zundrel.conveyance.common.blocks.entities.InserterBlockEntity;
import net.minecraft.item.ItemStack;

public class InserterExtractable implements ItemExtractable {
	protected final InserterBlockEntity delegate;
	
	public InserterExtractable(InserterBlockEntity delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public ItemStack attemptExtraction(ItemFilter filter, int maxAmount, Simulation simulation) {
		if (!delegate.getStack().isEmpty()) {
			if (filter.matches(delegate.getStack())) {
				if (maxAmount>delegate.getStack().getCount()) {
					ItemStack extracted = delegate.getStack().copy();
					extracted.setCount(maxAmount);
					
					delegate.getStack().decrement(maxAmount);
					delegate.markDirty();
					
					return extracted;
				} else {
					ItemStack extracted = delegate.getStack();
					
					delegate.removeStack();
					
					return extracted;
				}
			}
		}
		
		return ItemStack.EMPTY;
	}

}