package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.inventory.ConveyorInventory;
import com.zundrel.conveyance.common.registries.ModBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class ConveyorBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity, ConveyorInventory, Tickable {
    protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected boolean front = false;
    protected boolean down = false;
    protected boolean across = false;
    protected int position = 0;
    protected int prevPosition = 0;

    public ConveyorBlockEntity() {
        super(ModBlockEntities.CONVEYOR);
    }

    public ConveyorBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);

        if (!isInvEmpty() && front && across && getWorld().getBlockEntity(getPos().offset(direction)) instanceof ConveyorBlockEntity && getWorld().getBlockEntity(getPos().offset(direction).offset(direction)) instanceof ConveyorBlockEntity) {
            advancePositionAcross(getPos().offset(direction));
        } else if (!isInvEmpty() && front && !across && getWorld().getBlockEntity(getPos().offset(direction)) instanceof ConveyorBlockEntity) {
            advancePosition(getPos().offset(direction));
        } else if (!isInvEmpty() && down && getWorld().getBlockEntity(getPos().offset(direction).down()) instanceof DownVerticalConveyorBlockEntity) {
            advancePosition(getPos().offset(direction).down());
        } else if (position > 0) {
            setPosition(Math.max(0, position - 4));
        }
    }

    public void advancePosition(BlockPos pos) {
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos);
        boolean empty = conveyorBlockEntity.isInvEmpty();

        if (!getWorld().isClient() && this.position >= 16 && conveyorBlockEntity.isInvEmpty()) {
            conveyorBlockEntity.setInvStack(0, getInvStack(0));
            removeInvStack(0);
        }

        if (empty && this.position < 16 || !empty && this.position < 16 && this.position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(this.position + 1);
        } else {
            prevPosition = this.position;
        }

        if (this.position > 0 && !conveyorBlockEntity.isInvEmpty() && conveyorBlockEntity.getPosition() == 0) {
            setPosition(Math.max(0, this.position - 4));
        }
    }

    public void advancePositionAcross(BlockPos pos) {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos);
        ConveyorBlockEntity acrossBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos.offset(direction));
        boolean empty = conveyorBlockEntity.isInvEmpty();

        if (!getWorld().isClient() && this.position >= 16 && conveyorBlockEntity.isInvEmpty()) {
            conveyorBlockEntity.setInvStack(0, getInvStack(0));
            removeInvStack(0);
        }

        if (empty && acrossBlockEntity.getPosition() == 0 && this.position < 16 || !empty && acrossBlockEntity.getPosition() == 0 && this.position < 16 && this.position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(this.position + 1);
        } else {
            prevPosition = this.position;
        }

        if (this.position > 0 && !conveyorBlockEntity.isInvEmpty() && conveyorBlockEntity.getPosition() == 0) {
            setPosition(Math.max(0, this.position - 4));
        }
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

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            for (Object obj : PlayerStream.watching(this).toArray()) {
                ServerPlayerEntity player = (ServerPlayerEntity) obj;
                player.networkHandler.sendPacket(this.toUpdatePacket());
            }
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag_1) {
        super.fromTag(compoundTag_1);
        items.clear();
        Inventories.fromTag(compoundTag_1, items);
        front = compoundTag_1.getBoolean("front");
        down = compoundTag_1.getBoolean("down");
        across = compoundTag_1.getBoolean("across");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag_1) {
        Inventories.toTag(compoundTag_1, items);
        compoundTag_1.putBoolean("front", front);
        compoundTag_1.putBoolean("down", down);
        compoundTag_1.putBoolean("across", across);
        return super.toTag(compoundTag_1);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
