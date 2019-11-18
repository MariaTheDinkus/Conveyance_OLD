package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.common.blocks.conveyors.ConveyorProperties;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VerticalConveyorBlockEntity extends ConveyorBlockEntity {
    protected boolean up = false;
    protected int horizontalPosition;
    protected int prevHorizontalPosition;

    public VerticalConveyorBlockEntity() {
        super(ConveyanceBlockEntities.VERTICAL_CONVEYOR);
    }

    public VerticalConveyorBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        boolean conveyor = getCachedState().get(ConveyorProperties.CONVEYOR);
        int speed = ((IConveyor) getCachedState().getBlock()).getSpeed();

        if (!isEmpty() && up && getWorld().getBlockEntity(getPos().up()) instanceof VerticalConveyorBlockEntity) {
            advancePosition(getPos().up());
        } else if (!isEmpty() && conveyor && getWorld().getBlockEntity(getPos().offset(direction).up()) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction).up());
            boolean empty = conveyorBlockEntity.isEmpty();

            if (!getWorld().isClient() && position >= speed && horizontalPosition >= speed / 2 && conveyorBlockEntity.isEmpty()) {
                conveyorBlockEntity.setStack(getStack());
                removeStack();
            }

            if (empty && position < speed || !empty && position < speed && conveyorBlockEntity.getPosition() > 4) {
                setPosition(position + 1);
            } else {
                prevPosition = position;
            }

            if (empty && horizontalPosition < speed / 2 && position >= speed || !empty && horizontalPosition < speed / 2 && position >= speed - 1 && conveyorBlockEntity.getPosition() > 4) {
                setHorizontalPosition(horizontalPosition + 1);
            } else {
                prevHorizontalPosition = horizontalPosition;
            }
        } else {
            if (horizontalPosition > 0) {
                horizontalPosition = 0;
            }

            if (position > 0) {
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
        VerticalConveyorBlockEntity conveyorBlockEntity = (VerticalConveyorBlockEntity) getWorld().getBlockEntity(pos);
        boolean empty = conveyorBlockEntity.isEmpty();
        int speed = ((IConveyor) getCachedState().getBlock()).getSpeed();

        if (position >= speed && conveyorBlockEntity.isEmpty()) {
            conveyorBlockEntity.setStack(getStack());
            conveyorBlockEntity.setHorizontalPosition(0);
            removeStack();
        }

        if (empty && position < speed || !empty && position < speed && position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(position + 1);
        } else {
            prevPosition = position;
        }

        if (position > 0 && !conveyorBlockEntity.isEmpty() && conveyorBlockEntity.getPosition() == 0) {
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

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        up = compoundTag.getBoolean("up");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.putBoolean("up", up);
        return super.toTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
