package com.zundrel.conveyance.common.blocks.entities;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import alexiil.mc.lib.attributes.item.entity.ItemEntityAttributeUtil;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;
import alexiil.mc.lib.attributes.item.impl.RejectingItemInsertable;
import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.conveyors.InserterBlock;
import com.zundrel.conveyance.common.inventory.SingularStackInventory;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class InserterBlockEntity extends BlockEntity implements SingularStackInventory, BlockEntityClientSerializable, RenderAttachmentBlockEntity, Tickable {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected int position = 0;
    protected int prevPosition = 0;

    public InserterBlockEntity() {
        super(ConveyanceBlockEntities.INSERTER);
    }

    public InserterBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
		Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
		boolean powered = getCachedState().get(Properties.POWERED);
		int speed = ((InserterBlock) getCachedState().getBlock()).getSpeed();

		if (!powered) {
			if (isEmpty()) {
				BlockState behindState = world.getBlockState(getPos().offset(direction.getOpposite()));
				ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(world, getPos().offset(direction.getOpposite()), SearchOptions.inDirection(direction.getOpposite()));

				if (behindState.getBlock() instanceof AbstractFurnaceBlock) {
					extractable = ItemAttributes.EXTRACTABLE.get(world, getPos().offset(direction.getOpposite()), SearchOptions.inDirection(Direction.UP));
				}

				if (extractable != EmptyItemExtractable.NULL) {
					ItemStack stack = extractable.attemptAnyExtraction(64, Simulation.SIMULATE);
					if (position == 0 && !stack.isEmpty()) {
						stack = extractable.attemptAnyExtraction(64, Simulation.ACTION);
						setStack(stack);
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				} else {
					BlockPos offsetPos = getPos().offset(direction.getOpposite());
					List<ChestMinecartEntity> minecartEntities = getWorld().getEntities(ChestMinecartEntity.class, new Box(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), offsetPos.getX() + 1, offsetPos.getY() + 1, offsetPos.getZ() + 1), EntityPredicates.EXCEPT_SPECTATOR);
					if (position == 0 && minecartEntities.size() >= 1) {
						ChestMinecartEntity minecartEntity = minecartEntities.get(0);
						FixedInventoryVanillaWrapper wrapper = new FixedInventoryVanillaWrapper(minecartEntity);
						ItemExtractable extractableMinecart = wrapper.getExtractable();

						ItemStack stackMinecart = extractableMinecart.attemptAnyExtraction(64, Simulation.SIMULATE);
						if (position == 0 && !stackMinecart.isEmpty()) {
							stackMinecart = extractableMinecart.attemptAnyExtraction(64, Simulation.ACTION);
							setStack(stackMinecart);
							minecartEntity.markDirty();
						}
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				}
			} else if (!isEmpty()) {
				Inventory inventory = (Inventory) getWorld().getBlockEntity(getPos().offset(direction));
				BlockState aheadState = getWorld().getBlockState(getPos().offset(direction));

				ItemInsertable insertable = ItemAttributes.INSERTABLE.get(world, getPos().offset(direction), SearchOptions.inDirection(direction));

				if (aheadState.getBlock() instanceof ComposterBlock) {
					insertable = ItemAttributes.INSERTABLE.get(world, getPos().offset(direction), SearchOptions.inDirection(Direction.DOWN));
				} else if (aheadState.getBlock() instanceof AbstractFurnaceBlock && !AbstractFurnaceBlockEntity.canUseAsFuel(getStack())) {
					insertable = ItemAttributes.INSERTABLE.get(world, getPos().offset(direction), SearchOptions.inDirection(Direction.DOWN));
				}

				ItemStack stack = insertable.attemptInsertion(getStack(), Simulation.SIMULATE);
				if (insertable != RejectingItemInsertable.NULL) {
					if (stack.isEmpty() || stack.getCount() != getStack().getCount()) {
						if (position < speed) {
							setPosition(getPosition() + 1);
						} else if (!getWorld().isClient()) {
							stack = insertable.attemptInsertion(getStack(), Simulation.ACTION);
							setStack(stack);
						}
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				} else {
					BlockPos offsetPos = getPos().offset(direction);
					List<ChestMinecartEntity> minecartEntities = getWorld().getEntities(ChestMinecartEntity.class, new Box(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), offsetPos.getX() + 1, offsetPos.getY() + 1, offsetPos.getZ() + 1), EntityPredicates.EXCEPT_SPECTATOR);
					if (minecartEntities.size() >= 1) {
						ChestMinecartEntity minecartEntity = minecartEntities.get(0);
						FixedInventoryVanillaWrapper wrapper = new FixedInventoryVanillaWrapper(minecartEntity);
						ItemInsertable insertableMinecart = wrapper.getInsertable();

						ItemStack stackMinecart = insertableMinecart.attemptInsertion(getStack(), Simulation.SIMULATE);
						if (position < speed && (stackMinecart.isEmpty() || stackMinecart.getCount() != getStack().getCount())) {
							setPosition(getPosition() + 1);
						} else if (!getWorld().isClient() && (stackMinecart.isEmpty() || stackMinecart.getCount() != getStack().getCount())) {
							stackMinecart = insertableMinecart.attemptInsertion(getStack(), Simulation.ACTION);
							setStack(stackMinecart);
							minecartEntity.markDirty();
						}
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				}
			} else if (position > 0) {
				setPosition(getPosition() - 1);
			}
		} else if (position > 0) {
			setPosition(getPosition() - 1);
		}
    }

//	public boolean hasInput() {
//		return hasInput;
//	}
//
//	public boolean hasOutput() {
//		return hasOutput;
//	}
//
//	public void setHasInput(boolean hasInput) {
//		this.hasInput = hasInput;
//	}
//
//	public void setHasOutput(boolean hasOutput) {
//		this.hasOutput = hasOutput;
//	}

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

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtractInvStack(slot, stack, facing);
	}

	private static boolean extract(SingularStackInventory singularStackInventory, Inventory inventory, int slot, Direction side) {
		ItemStack itemStack = inventory.getInvStack(slot);
		if (!itemStack.isEmpty() && canExtract(inventory, itemStack, slot, side)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = transfer(inventory, singularStackInventory, inventory.takeInvStack(slot, inventory.getInvStack(slot).getCount()), (Direction)null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setInvStack(slot, itemStack2);
		}

		return false;
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

	@Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

	@Override
	public int getInvSize() {
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
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        clear();
        setStack(ItemStack.fromTag(compoundTag.getCompound("stack")));
        position = compoundTag.getInt("position");
//        hasInput = compoundTag.getBoolean("hasInput");
//        hasOutput = compoundTag.getBoolean("hasOutput");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.put("stack", getStack().toTag(new CompoundTag()));
        compoundTag.putInt("position", position);
//        compoundTag.putBoolean("hasInput", hasInput);
//        compoundTag.putBoolean("hasOutput", hasOutput);
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
