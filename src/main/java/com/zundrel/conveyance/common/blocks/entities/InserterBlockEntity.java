package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.common.blocks.conveyors.InserterBlock;
import com.zundrel.conveyance.common.inventory.SingularStackInventory;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.stream.IntStream;

public class InserterBlockEntity extends BlockEntity implements SingularStackInventory, BlockEntityClientSerializable, RenderAttachmentBlockEntity, Tickable {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected int position = 0;
    protected int prevPosition = 0;
    protected boolean hasInput = false;
    protected boolean hasOutput = false;

    public InserterBlockEntity() {
        super(ConveyanceBlockEntities.INSERTER);
    }

    public InserterBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
		Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
		int speed = ((InserterBlock) getCachedState().getBlock()).getSpeed();

		if (isEmpty() && hasInput && getWorld().getBlockEntity(getPos().offset(direction.getOpposite())) instanceof Inventory) {
			Inventory inventory = (Inventory) getWorld().getBlockEntity(getPos().offset(direction.getOpposite()));
			if (position == 0) {
				int slotToUse = -1;
				for (int i = 0; i < inventory.size(); i++) {
					if (!inventory.getStack(i).isEmpty()) {
						slotToUse = i;
						break;
					}
				}

				if (slotToUse != -1)
					extract(this, inventory, slotToUse, direction);
			} else if (position > 0) {
				setPosition(getPosition() - 1);
			}
		} else if (!isEmpty() && hasOutput && getWorld().getBlockEntity(getPos().offset(direction)) instanceof Inventory && !this.isInventoryFull((Inventory) getWorld().getBlockEntity(getPos().offset(direction)), direction.getOpposite())) {
			Inventory inventory = (Inventory) getWorld().getBlockEntity(getPos().offset(direction));

			if (position < speed) {
				setPosition(getPosition() + 1);
			} else {
				ItemStack itemStack2 = transfer(this, inventory, this.removeStack(0, getStack().getCount()), direction.getOpposite());
				if (itemStack2.isEmpty()) {
					inventory.markDirty();
				}
			}
		} else if (position > 0) {
			setPosition(getPosition() - 1);
		}
    }

	public boolean hasInput() {
		return hasInput;
	}

	public boolean hasOutput() {
		return hasOutput;
	}

	public void setHasInput(boolean hasInput) {
		this.hasInput = hasInput;
	}

	public void setHasOutput(boolean hasOutput) {
		this.hasOutput = hasOutput;
	}

	private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
		return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory)inventory).getAvailableSlots(side)) : IntStream.range(0, inventory.size());
	}

	private boolean isInventoryFull(Inventory inv, Direction direction) {
		return getAvailableSlots(inv, direction).allMatch((i) -> {
			ItemStack itemStack = inv.getStack(i);
			return itemStack.getCount() >= itemStack.getMaxCount();
		});
	}

	public static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, Direction side) {
		if (to instanceof SidedInventory && side != null) {
			SidedInventory sidedInventory = (SidedInventory)to;
			int[] is = sidedInventory.getAvailableSlots(side);

			for(int i = 0; i < is.length && !stack.isEmpty(); ++i) {
				stack = transfer(from, to, stack, is[i], side);
			}
		} else {
			int j = to.size();

			for(int k = 0; k < j && !stack.isEmpty(); ++k) {
				stack = transfer(from, to, stack, k, side);
			}
		}

		return stack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, Direction side) {
		if (!inventory.isValid(slot, stack)) {
			return false;
		} else {
			return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(slot, stack, side);
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

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtract(slot, stack, facing);
	}

	private static boolean extract(SingularStackInventory singularStackInventory, Inventory inventory, int slot, Direction side) {
		ItemStack itemStack = inventory.getStack(slot);
		if (!itemStack.isEmpty() && canExtract(inventory, itemStack, slot, side)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = transfer(inventory, singularStackInventory, inventory.removeStack(slot, inventory.getStack(slot).getCount()), (Direction)null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setStack(slot, itemStack2);
		}

		return false;
	}

	private static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction) {
		ItemStack itemStack = to.getStack(slot);
		if (canInsert(to, stack, slot, direction)) {
			boolean bl = false;
			boolean bl2 = to.isEmpty();
			if (itemStack.isEmpty()) {
				to.setStack(slot, stack);
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

	@Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

	@Override
	public int size() {
		return 1;
	}

	@Override
    public ItemStack removeStack() {
        position = 15;
        prevPosition = 15;
        return SingularStackInventory.super.removeStack();
    }

    @Override
    public int[] getRenderAttachmentData() {
        return new int[] { position, prevPosition };
    }


    public int getPosition() {
        return position;
    }

    public int getPrevPosition() {
        return prevPosition;
    }

    public void setPosition(int position) {
        if (position == 0)
            this.prevPosition = 0;
        else
            this.prevPosition = this.position;
        this.position = position;
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
    public void fromTag(BlockState state, CompoundTag compoundTag) {
        super.fromTag(state, compoundTag);
        clear();
        setStack(ItemStack.fromTag(compoundTag.getCompound("stack")));
        position = compoundTag.getInt("position");
        hasInput = compoundTag.getBoolean("hasInput");
        hasOutput = compoundTag.getBoolean("hasOutput");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(getCachedState(), compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.put("stack", getStack().toTag(new CompoundTag()));
        compoundTag.putInt("position", position);
        compoundTag.putBoolean("hasInput", hasInput);
        compoundTag.putBoolean("hasOutput", hasOutput);
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
