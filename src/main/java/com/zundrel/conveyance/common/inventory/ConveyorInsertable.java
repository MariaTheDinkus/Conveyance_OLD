package com.zundrel.conveyance.common.inventory;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.minecraft.item.ItemStack;

public class ConveyorInsertable implements ItemInsertable {
    protected final ConveyorBlockEntity conveyor;

    public ConveyorInsertable(ConveyorBlockEntity conveyor) {
        this.conveyor = conveyor;
    }

    @Override
    public ItemStack attemptInsertion(ItemStack itemStack, Simulation simulation) {
        if (conveyor.isEmpty()) {
            if (simulation==Simulation.ACTION) {
                conveyor.setStack(itemStack.copy());
            }
            return ItemStack.EMPTY;
        } else {
            return itemStack;
        }
    }
}
