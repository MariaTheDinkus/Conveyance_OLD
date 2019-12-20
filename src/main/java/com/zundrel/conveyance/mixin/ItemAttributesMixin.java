/*
 * From Logistics Pipes by RS485. Used with permission.
 */

package com.zundrel.conveyance.mixin;

import alexiil.mc.lib.attributes.CustomAttributeAdder;
import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import alexiil.mc.lib.attributes.item.compat.FixedSidedInventoryVanillaWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Function;

@Mixin(ItemAttributes.class)
public class ItemAttributesMixin {

	// FIXME remove when updating LBA

	@Overwrite
	private static <T> CustomAttributeAdder<T> createBlockAdder(Function<FixedItemInv, T> convertor) {
		return (world, pos, state, list) -> {
			Block block = state.getBlock();
			Direction direction = list.getSearchDirection();
			Direction blockSide = direction == null ? null : direction.getOpposite();
			SidedInventory sidedInv;
			FixedItemInv wrapper;
			if (block instanceof InventoryProvider) {
				InventoryProvider provider = (InventoryProvider) block;
				sidedInv = provider.getInventory(state, world, pos);
				if (sidedInv != null) {
					if (sidedInv.getInvSize() > 0) {
						if (direction != null) {
							wrapper = FixedSidedInventoryVanillaWrapper.create(sidedInv, blockSide);
						} else {
							wrapper = new FixedInventoryVanillaWrapper(sidedInv);
						}

						list.add(convertor.apply(wrapper));
					} else {
						list.add((T) ((DefaultedAttribute) list.attribute).defaultValue);
					}
				}
			} else if (block.hasBlockEntity()) {
				BlockEntity be = world.getBlockEntity(pos);
				if (be instanceof ChestBlockEntity) {
					boolean checkForBlockingCats = false;
					Inventory chestInv = ChestBlock.getInventory((ChestBlock) state.getBlock(), state, world, pos, checkForBlockingCats);
					if (chestInv != null) {
						list.add(convertor.apply(new FixedInventoryVanillaWrapper(chestInv)));
					}
				} else if (be instanceof SidedInventory) {
					sidedInv = (SidedInventory) be;
					if (direction != null) {
						wrapper = FixedSidedInventoryVanillaWrapper.create(sidedInv, blockSide);
					} else {
						wrapper = new FixedInventoryVanillaWrapper(sidedInv);
					}

					list.add(convertor.apply(wrapper));
				} else if (be instanceof Inventory) {
					list.add(convertor.apply(new FixedInventoryVanillaWrapper((Inventory) be)));
				}
			}

		};
	}

}