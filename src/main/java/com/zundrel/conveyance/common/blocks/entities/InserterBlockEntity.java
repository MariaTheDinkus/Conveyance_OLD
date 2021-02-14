package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.common.blocks.conveyors.InserterBlock;
import com.zundrel.conveyance.common.inventory.SingularStackInventory;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class InserterBlockEntity extends BlockEntity implements SingularStackInventory, BlockEntityClientSerializable, RenderAttachmentBlockEntity, Tickable {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected int position = 0;
    protected int prevPosition = 0;
    protected boolean hasInput = false;
    protected boolean hasOutput = false;
    protected String filterItem = "";

    public InserterBlockEntity() {
        super(ConveyanceBlockEntities.INSERTER);
    }

    @Override
    public void tick() {

        // If this block receives a redstone signal, we don't have to do anything
        if (Objects.requireNonNull(getWorld()).isReceivingRedstonePower(getPos())) {
            if (position > 0) {
                setPosition(getPosition() - 1);
            }
            return;
        }

        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        int speed = ((InserterBlock) getCachedState().getBlock()).getSpeed();

        if (isEmpty() &&
                hasInput &&
                !getWorld().isClient() &&
                getWorld().getBlockEntity(getPos().offset(direction.getOpposite())) instanceof Inventory
        ) {
            // Source
            Inventory inventory = (Inventory) getWorld().getBlockEntity(getPos().offset(direction.getOpposite()));

            // Get a slot with something to move
            if (position == 0) {
                int slotToUse = getSourceSlot(inventory);

                // Found a slot with a usable stack, let's check if we can use it
                if (slotToUse != -1) {
                    if (getWorld().getBlockEntity(getPos().offset(direction)) instanceof Inventory) {
                        Inventory targetInventory = (Inventory) getWorld().getBlockEntity(getPos().offset(direction));
                        // Check if we can insert in slot 0
                        if (canInsert(targetInventory, inventory.getStack(slotToUse), 0, direction)) {
                            // Move stack to own inventory
                            extract(this, inventory, slotToUse, direction.getOpposite());
                        }
                    }
                }
            } else if (position > 0) {
                setPosition(getPosition() - 1);
            }
        } else if (
                !isEmpty() &&
                        hasOutput &&
                        getWorld().getBlockEntity(getPos().offset(direction)) instanceof Inventory
        ) {
            // Move from internal to target
            Inventory inventory = (Inventory) getWorld().getBlockEntity(getPos().offset(direction));

            if (position < speed) {
                setPosition(getPosition() + 1);
            } else if (!getWorld().isClient()) {

                int targetSlot = -1;

                if (inventory instanceof SidedInventory) {
                    targetSlot = getTargetSlotForSidedInventory((SidedInventory) inventory, this.getStack(), direction.getOpposite());
                } else {
                    targetSlot = getTargetSlot(inventory, this.getStack(), direction.getOpposite());
                }

                if (targetSlot > -1) {
                    ItemStack itemStack2 = transfer(this, inventory, this.removeStack(0, getStack().getCount()), targetSlot, direction.getOpposite());
                    if (itemStack2.isEmpty()) {
                        inventory.markDirty();
                    }
                }
            }
        } else if (position > 0) {
            setPosition(getPosition() - 1);
        }
    }

    /**
     * This function returns a possible slot to take items from. It checks for a filter on the inserter
     * and a slot in the inventory to take the items from.
     *
     * @param inventory The inventory from where to take items
     * @return int The slot-id when one is found, or -1 when no slot is found
     */
    private int getSourceSlot(Inventory inventory) {
        int slotToUse = -1;

        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.getStack(i).isEmpty()) {
                if (!hasFilterItem()) {
                    slotToUse = i;
                    return slotToUse;
                } else {
                    if (inventory.getStack(i).getTranslationKey().equals(getFilterItem())) {
                        slotToUse = i;
                        return slotToUse;
                    }
                }
            }
        }
        return slotToUse;
    }


    /**
     * Returns a targetslot, or -1, for a target-inventory
     *
     * @param inventory   The inventory that needs to be checked
     * @param sourceStack The sourcestack which needs to be stored
     * @param direction   The facing direction of the receiving end
     * @return int
     */
    private static int getTargetSlot(Inventory inventory, ItemStack sourceStack, Direction direction) {
        int targetSlot = -1;

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).getItem() == sourceStack.getItem()) {
                int availableSpace = inventory.getStack(i).getMaxCount() - inventory.getStack(i).getCount();
                if (availableSpace > 0 && inventory.isValid(i, sourceStack)) {
                    targetSlot = i;
                    break;
                }
            }
            if (inventory.getStack(i).isEmpty() && inventory.isValid(i, sourceStack)) {
                targetSlot = i;
                break;
            }
        }
        return targetSlot;
    }

    /**
     * Returns a targetslot, or -1, for a target-inventory classed as SidedInventory
     *
     * @param inventory   The inventory that needs to be checked
     * @param sourceStack The sourcestack which needs to be stored
     * @param direction   The facing direction of the receiving end
     * @return int
     */
    private static int getTargetSlotForSidedInventory(SidedInventory inventory, ItemStack sourceStack, Direction direction) {
        int targetSlot = -1;

        int[] availableSlots = inventory.getAvailableSlots(direction);

        for (int i = 0; i < availableSlots.length; i++) {
            if (inventory.getStack(i).getItem() == sourceStack.getItem()) {
                int availableSpace = inventory.getStack(i).getMaxCount() - inventory.getStack(i).getCount();
                if (availableSpace > 0 && inventory.canInsert(i, sourceStack, direction)) {
                    targetSlot = i;
                    break;
                }
            }

            if (inventory.getStack(i).isEmpty() && inventory.canInsert(i, sourceStack, direction)) {
                targetSlot = i;
                break;
            }

        }
        return targetSlot;
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

    public static ItemStack transfer(Inventory fromInventory, Inventory toInventory, ItemStack stack, Direction side) {
        if (toInventory instanceof SidedInventory && side != null) {
            SidedInventory sidedInventory = (SidedInventory) toInventory;
            int[] is = sidedInventory.getAvailableSlots(side);

            for (int i = 0; i < is.length && !stack.isEmpty(); ++i) {
                stack = transfer(fromInventory, toInventory, stack, is[i], side);
            }
        } else {
            int j = toInventory.size();

            for (int k = 0; k < j && !stack.isEmpty(); ++k) {
                stack = transfer(fromInventory, toInventory, stack, k, side);
            }
        }

        return stack;
    }

    /**
     * @param inventory The Target Inventory
     * @param stack     The Stack To insert
     * @param slot      The slot in which to insert
     * @param side      The side (which we don't use now)
     * @return boolean
     */
    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, Direction side) {
        if (inventory instanceof SidedInventory) {
            return getTargetSlotForSidedInventory((SidedInventory) inventory, stack, side) > -1;
        } else {
            return getTargetSlot(inventory, stack, side) > -1;
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
        return !(inv instanceof SidedInventory) || ((SidedInventory) inv).canExtract(slot, stack, facing);
    }

    private static boolean extract(SingularStackInventory singularStackInventory, Inventory sourceInventory, int slot, Direction side) {
        ItemStack itemStackFrom = sourceInventory.getStack(slot);

        if (!itemStackFrom.isEmpty() && canExtract(sourceInventory, itemStackFrom, slot, side)) {

            // Make a backup to be able to reset everything
            ItemStack stackToBeMoved = sourceInventory.getStack(slot).copy();

            // Move the stack
            ItemStack movedStack = transfer(sourceInventory, singularStackInventory, stackToBeMoved, (Direction) null);

            // Everything moved? Let's remove the source and mark it dirty
            if (movedStack.isEmpty()) {
                sourceInventory.removeStack(slot);
            } else {
                sourceInventory.setStack(slot, movedStack);
            }
            sourceInventory.markDirty();
            return true;
        }

        return false;
    }

    private static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction) {
        ItemStack itemStack = to.getStack(slot);

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
        return new int[]{position, prevPosition};
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



    public String getFilterItem() {
        return filterItem;
    }

    public void setFilterItem(Item filterItem) {
        this.filterItem = filterItem.getTranslationKey();
    }

    public void clearFilterItem() {
        this.filterItem = "";
    }

    public boolean hasFilterItem() {
        return !(this.getFilterItem().isEmpty());
    }

    public void sync() {
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).getChunkManager().markForUpdate(pos);
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
        filterItem = compoundTag.getString("filterItem");
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
        compoundTag.putString("filterItem", filterItem);
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
