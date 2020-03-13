package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.common.inventory.SingularStackSidedInventory;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.stream.IntStream;

public class InserterBlockEntity extends BlockEntity implements SingularStackSidedInventory, BlockEntityClientSerializable, Tickable {
	private DefaultedList<ItemStack> inventory;
	private int transferCooldown = 0;

    public InserterBlockEntity() {
        super(ConveyanceBlockEntities.INSERTER);
		this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public InserterBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);

        if (!getWorld().isClient() && !isInvEmpty() && transferCooldown == 0) {
			if (getWorld().getBlockEntity(getPos().offset(direction)) instanceof Inventory) {
				Inventory inventory = (Inventory) getWorld().getBlockEntity(getPos().offset(direction));

				Direction inputSide = direction.getOpposite();
				if (!this.isInventoryFull(inventory, inputSide)) {
					if (!isEmpty()) {
						//ItemStack itemStack = this.getStack().copy();
						ItemStack itemStack2 = transfer(this, inventory, this.takeInvStack(0, getStack().getCount()), inputSide);
						if (itemStack2.isEmpty()) {
							inventory.markDirty();
						}

						//this.setStack(itemStack);
					}
				}
			}
        }

		transferCooldown = (transferCooldown + 1) % 15;
    }

    Direction getDirection() {
		return getCachedState().get(HorizontalFacingBlock.FACING);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	public Direction[] getInsertionSides() {
		return new Direction[] { getDirection().getOpposite() };
	}

	@Override
	public Direction[] getExtractionSides() {
		return new Direction[] { getDirection().rotateYClockwise(), getDirection().rotateYCounterclockwise() };
	}

	private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
		return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory)inventory).getInvAvailableSlots(side)) : IntStream.range(0, inventory.getInvSize());
	}

	private boolean isInventoryFull(Inventory inv, Direction direction) {
		return getAvailableSlots(inv, direction).allMatch((i) -> {
			ItemStack itemStack = inv.getInvStack(i);
			return itemStack.getCount() >= itemStack.getMaxCount();
		});
	}

	public static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, Direction side) {
		if (to instanceof SidedInventory && side != null) {
			SidedInventory sidedInventory = (SidedInventory)to;
			int[] is = sidedInventory.getInvAvailableSlots(side);

			for(int i = 0; i < is.length && !stack.isEmpty(); ++i) {
				stack = transfer(from, to, stack, is[i], side);
			}
		} else {
			int j = to.getInvSize();

			for(int k = 0; k < j && !stack.isEmpty(); ++k) {
				stack = transfer(from, to, stack, k, side);
			}
		}

		return stack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, Direction side) {
		if (!inventory.isValidInvStack(slot, stack)) {
			return false;
		} else {
			return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(slot, stack, side);
		}
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamage() != second.getDamage()) {
			return false;
		} else if (first.getCount() > first.getMaxCount()) {
			return false;
		} else {
			return ItemStack.areTagsEqual(first, second);
		}
	}

	private static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction) {
		ItemStack itemStack = to.getInvStack(slot);
		if (canInsert(to, stack, slot, direction)) {
			boolean bl = false;
			boolean bl2 = to.isInvEmpty();
			if (itemStack.isEmpty()) {
				to.setInvStack(slot, stack);
				stack = ItemStack.EMPTY;
				bl = true;
			} else if (canMergeItems(itemStack, stack)) {
				int i = stack.getMaxCount() - itemStack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.decrement(j);
				itemStack.increment(j);
				bl = j > 0;
			}
		}

		return stack;
	}

	public void sync() {
        if (world instanceof ServerWorld) {
            ((ServerWorld)world).getChunkManager().markForUpdate(pos);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sync();
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
		this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		Inventories.fromTag(compoundTag, this.inventory);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
		Inventories.toTag(compoundTag, this.inventory);
        return super.toTag(compoundTag);
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return toTag(new CompoundTag());
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
