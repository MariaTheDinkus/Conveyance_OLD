package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.common.inventory.ConveyorInventory;
import com.zundrel.conveyance.common.registries.ModBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class ConveyorBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity, ConveyorInventory, Tickable {
    protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected boolean front = false;
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

        if (!isInvEmpty() && front && getWorld().getBlockEntity(getPos().offset(direction)) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction));
            ItemStack stack = conveyorBlockEntity.getInvStack(0);
            boolean empty = conveyorBlockEntity.isInvEmpty();

            if (!getWorld().isClient() && position >= 16 && conveyorBlockEntity.isInvEmpty()) {
                conveyorBlockEntity.setInvStack(0, getInvStack(0));
                removeInvStack(0);
            }

            if (empty && position < 16 || !empty && position < 16 && position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
                setPosition(position + 1);
            } else {
                prevPosition = position;
            }

            if (!conveyorBlockEntity.isInvEmpty() && conveyorBlockEntity.getPosition() == 0) {
                setPosition(0);
            }
        }

        if (isInvEmpty() && position > 0 || !front && position > 0) {
            setPosition(0);
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
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        BlockEntityUpdateS2CPacket packet = new BlockEntityUpdateS2CPacket(getPos(), 127, toClientTag(new CompoundTag()));

        return packet;
    }

    @Override
    public void fromTag(CompoundTag compoundTag_1) {
        super.fromTag(compoundTag_1);
        items.clear();
        Inventories.fromTag(compoundTag_1, items);
        front = compoundTag_1.getBoolean("front");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag_1) {
        Inventories.toTag(compoundTag_1, items);
        compoundTag_1.putBoolean("front", front);
        return super.toTag(compoundTag_1);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
