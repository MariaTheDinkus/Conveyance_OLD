package com.zundrel.conveyance.common.inventory;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import com.zundrel.conveyance.common.blocks.entities.InserterBlockEntity;
import net.minecraft.item.ItemStack;

public class InserterInsertable implements ItemInsertable {
	protected final InserterBlockEntity delegate;
	
	public InserterInsertable(InserterBlockEntity delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
		if (delegate.getStack().isEmpty()) {
			if (simulation==Simulation.ACTION) {
				delegate.setStack(stack.copy());
			}
			return ItemStack.EMPTY;
		} else {
			return stack;
		}
	}

}