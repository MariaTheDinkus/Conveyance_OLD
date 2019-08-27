package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.ConveyorProperties;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VerticalConveyorBlockEntity extends ConveyorBlockEntity {
    protected boolean up = false;
    protected int horizontalPosition;
    protected int prevHorizontalPosition;

    public VerticalConveyorBlockEntity() {
        super(ModBlockEntities.VERTICAL_CONVEYOR);
    }

    public VerticalConveyorBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
        tickConveyor();
    }

    private void tickConveyor() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        boolean conveyor = getCachedState().get(ConveyorProperties.CONVEYOR);

        if (!isInvEmpty() && up && getWorld().getBlockEntity(getPos().up()) instanceof VerticalConveyorBlockEntity) {
            advancePosition(getPos().up());
        } else if (!isInvEmpty() && conveyor && getWorld().getBlockEntity(getPos().offset(direction).up()) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction).up());
            boolean empty = conveyorBlockEntity.isInvEmpty();

            if (!getWorld().isClient() && position >= 16 && horizontalPosition >= 8 && conveyorBlockEntity.isInvEmpty()) {
                conveyorBlockEntity.setInvStack(0, getInvStack(0));
                removeInvStack(0);
            }

            if (empty && position < 16 || !empty && position < 16 && conveyorBlockEntity.getPosition() > 4) {
                setPosition(position + 1);
            } else {
                prevPosition = position;
            }

            if (empty && horizontalPosition < 8 && position >= 16 || !empty && horizontalPosition < 8 && position >= 15 && conveyorBlockEntity.getPosition() > 4) {
                setHorizontalPosition(horizontalPosition + 1);
            } else {
                prevHorizontalPosition = horizontalPosition;
            }
        } else {
            if (horizontalPosition > 0) {
                setPosition(horizontalPosition - 1);
            } else if (position > 0) {
                setPosition(position - 1);
            }
        }
    }

    public boolean hasUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
        markDirty();
    }

    public void advancePosition(BlockPos pos) {
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos);
        boolean empty = conveyorBlockEntity.isInvEmpty();

        if (position >= 16 && conveyorBlockEntity.isInvEmpty()) {
            conveyorBlockEntity.setInvStack(0, getInvStack(0));
            removeInvStack(0);
        }

        if (empty && position < 16 || !empty && position < 16 && position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(position + 1);
        } else {
            prevPosition = position;
        }

        if (position > 0 && !conveyorBlockEntity.isInvEmpty() && conveyorBlockEntity.getPosition() == 0) {
            setPosition(position - 1);
        }
    }

    @Override
    public int[] getRenderAttachmentData() {
        return new int[] { position, prevPosition, horizontalPosition, prevHorizontalPosition };
    }

    public int getHorizontalPosition() {
        return horizontalPosition;
    }

    public void setHorizontalPosition(int horizontalPosition) {
        if (horizontalPosition == 0)
            this.prevHorizontalPosition = 0;
        else
            this.prevHorizontalPosition = this.horizontalPosition;

        this.horizontalPosition = horizontalPosition;
    }

    public void setReverseHorizontalPosition(int position) {
        this.horizontalPosition = prevHorizontalPosition;
        if (position == 0)
            this.prevHorizontalPosition = 0;
        else
            this.prevHorizontalPosition = position;
    }

    @Override
    public void fromTag(CompoundTag compoundTag_1) {
        super.fromTag(compoundTag_1);
        items.clear();
        Inventories.fromTag(compoundTag_1, items);
        up = compoundTag_1.getBoolean("up");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag_1) {
        Inventories.toTag(compoundTag_1, items);
        compoundTag_1.putBoolean("up", up);
        return super.toTag(compoundTag_1);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
