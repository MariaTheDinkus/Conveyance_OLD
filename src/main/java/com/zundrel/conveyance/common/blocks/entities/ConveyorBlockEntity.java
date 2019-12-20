package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.api.IConveyorMachine;
import com.zundrel.conveyance.common.inventory.ConveyorInventory;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ConveyorBlockEntity extends BlockEntity implements ConveyorInventory, BlockEntityClientSerializable, RenderAttachmentBlockEntity, Tickable {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected boolean front = false;
    protected boolean down = false;
    protected boolean across = false;
    protected int position = 0;
    protected int prevPosition = 0;

    public ConveyorBlockEntity() {
        super(ConveyanceBlockEntities.CONVEYOR);
    }

    public ConveyorBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        int speed = ((IConveyor) getCachedState().getBlock()).getSpeed();

        if (!isEmpty() && front && across && getWorld().getBlockEntity(getPos().offset(direction)) instanceof ConveyorBlockEntity && getWorld().getBlockEntity(getPos().offset(direction).offset(direction)) instanceof ConveyorBlockEntity) {
            advancePositionAcross(getPos().offset(direction));
        } else if (!isEmpty() && front && getWorld().getBlockState(getPos().offset(direction)).getBlock() instanceof IConveyorMachine) {
            BlockPos posFacing = getPos().offset(direction);
            IConveyorMachine conveyorMachine = (IConveyorMachine) getWorld().getBlockState(posFacing).getBlock();

            boolean canInsert = conveyorMachine.canInsert(getWorld(), posFacing, getWorld().getBlockState(posFacing), this, getStack(), direction.getOpposite());

            if (!getWorld().isClient() && this.position >= speed && canInsert) {
                conveyorMachine.insert(getWorld(), posFacing, getWorld().getBlockState(posFacing), this, getStack(), direction.getOpposite());
                removeStack();
            }

            if (canInsert) {
                if (this.position < speed) {
                    setPosition(this.position + 1);
                }
            } else {
                if (this.position > 0) {
                    setPosition(Math.max(0, this.position - 4));
                    prevPosition = this.position;
                }
            }
        } else if (!isEmpty() && front && !across && getWorld().getBlockEntity(getPos().offset(direction)) instanceof ConveyorBlockEntity) {
            advancePosition(getPos().offset(direction));
        } else if (!isEmpty() && down && getWorld().getBlockEntity(getPos().offset(direction).down(1)) instanceof DownVerticalConveyorBlockEntity) {
            advancePositionDown(getPos().offset(direction).down(1));
        } else if (!isEmpty() && position > 0) {
            setPosition(Math.max(0, position - 4));
        } else if (isEmpty() && position > 0) {
            setPosition(0);
        }
    }

    public void advancePosition(BlockPos pos) {
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos);
        boolean empty = conveyorBlockEntity.isEmpty();
        int speed = ((IConveyor) getCachedState().getBlock()).getSpeed();

        if (!getWorld().isClient() && this.position >= speed && conveyorBlockEntity.isEmpty()) {
            conveyorBlockEntity.setStack(getStack());
            removeStack();
        }

        if (empty && this.position < speed || !empty && this.position < speed && this.position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(this.position + 1);
        } else {
            prevPosition = this.position;
        }

        if (this.position > 0 && !conveyorBlockEntity.isEmpty() && conveyorBlockEntity.getPosition() == 0) {
            setPosition(Math.max(0, this.position - 4));
        }
    }

    public void advancePositionDown(BlockPos pos) {
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos);
        boolean empty = conveyorBlockEntity.isEmpty();
        int speed = ((IConveyor) getCachedState().getBlock()).getSpeed();

        if (!getWorld().isClient() && this.position >= speed && conveyorBlockEntity.isEmpty()) {
            conveyorBlockEntity.setStack(getStack());
            removeStack();
        }

        if (empty && this.position < speed) {
            setPosition(this.position + 1);
        } else {
            prevPosition = this.position;
        }

        if (this.position > 0 && !conveyorBlockEntity.isEmpty() && conveyorBlockEntity.getPosition() == 0) {
            setPosition(Math.max(0, this.position - 4));
        }
    }

    public void advancePositionAcross(BlockPos pos) {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos);
        ConveyorBlockEntity acrossBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos.offset(direction));
        int speed = ((IConveyor) getCachedState().getBlock()).getSpeed();
        boolean empty = conveyorBlockEntity.isEmpty();

        if (!getWorld().isClient() && this.position >= 16 && conveyorBlockEntity.isEmpty()) {
            conveyorBlockEntity.setStack(getStack());
            removeStack();
        }

        if (empty && acrossBlockEntity.getPosition() == 0 && this.position < speed || !empty && acrossBlockEntity.getPosition() == 0 && this.position < speed && this.position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(this.position + 1);
        } else {
            prevPosition = this.position;
        }

        if (this.position > 0 && !conveyorBlockEntity.isEmpty() && conveyorBlockEntity.getPosition() == 0) {
            setPosition(Math.max(0, this.position - 4));
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    @Override
    public ItemStack removeStack() {
        position = 0;
        prevPosition = 0;
        return ConveyorInventory.super.removeStack();
    }

    @Override
    public int[] getRenderAttachmentData() {
        return new int[] { position, prevPosition };
    }

    public boolean hasFront() {
        return front;
    }

    public void setFront(boolean front) {
        this.front = front;
        markDirty();
    }

    public boolean hasDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
        markDirty();
    }

    public boolean hasAcross() {
        return across;
    }

    public void setAcross(boolean across) {
        this.across = across;
        markDirty();
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
        front = compoundTag.getBoolean("front");
        down = compoundTag.getBoolean("down");
        across = compoundTag.getBoolean("across");
        position = compoundTag.getInt("position");
        prevPosition = compoundTag.getInt("position");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.put("stack", getStack().toTag(new CompoundTag()));
        compoundTag.putBoolean("front", front);
        compoundTag.putBoolean("down", down);
        compoundTag.putBoolean("across", across);
        compoundTag.putInt("position", position);
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
