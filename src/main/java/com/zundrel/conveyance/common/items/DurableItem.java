package com.zundrel.conveyance.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

public class DurableItem extends Item {
	private ToolMaterial repairMaterial;

    public DurableItem(ToolMaterial repairMaterial,  Settings settings) {
        super(settings);

        this.repairMaterial = repairMaterial;
    }

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return repairMaterial.getRepairIngredient().test(ingredient);
	}
}
