package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.common.blocks.conveyors.ConveyorProperties;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DownVerticalConveyorBlockEntity extends ConveyorBlockEntity {
    protected boolean down = false;
    protected int horizontalPosition;
    protected int prevHorizontalPosition;

    public DownVerticalConveyorBlockEntity() {
        super(ConveyanceBlockEntities.DOWN_VERTICAL_CONVEYOR);
    }

    @Override
    public void tick() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        boolean front = getCachedState().get(ConveyorProperties.FRONT);
        boolean conveyor = getCachedState().get(ConveyorProperties.CONVEYOR);

        if (!isEmpty() && conveyor && !front && down && getWorld().getBlockEntity(getPos().down()) instanceof DownVerticalConveyorBlockEntity) {
            advancePosition(getPos().down(), 32);
        } else if (!isEmpty() && !conveyor && !front && down && getWorld().getBlockEntity(getPos().down()) instanceof DownVerticalConveyorBlockEntity) {
            advancePosition(getPos().down(), 16);
        } else if (!isEmpty() && conveyor && front && getWorld().getBlockEntity(getPos().offset(direction.getOpposite())) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction.getOpposite()));
            boolean empty = conveyorBlockEntity.isEmpty();

            if (!getWorld().isClient() && position >= 16 && horizontalPosition >= 16 && conveyorBlockEntity.isEmpty()) {
                conveyorBlockEntity.setStack(getStack());
                removeStack();
            }

            if (empty && position < 16  || !empty && position < 16 && conveyorBlockEntity.getPosition() > 4) {
                setPosition(position + 1);
            } else if (position > prevPosition) {
                prevPosition = position;
            }

            if (empty && position >= 16 && horizontalPosition < 16 || !empty && position >= 16 && horizontalPosition < 16 && conveyorBlockEntity.getPosition() > 4) {
                setHorizontalPosition(horizontalPosition + 1);
            } else if (horizontalPosition > prevHorizontalPosition) {
                prevHorizontalPosition = horizontalPosition;
            }
        } else if (!isEmpty() && !conveyor && front && getWorld().getBlockEntity(getPos().offset(direction.getOpposite())) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction.getOpposite()));
            boolean empty = conveyorBlockEntity.isEmpty();

            if (!getWorld().isClient() && horizontalPosition >= 16 && conveyorBlockEntity.isEmpty()) {
                conveyorBlockEntity.setStack(getStack());
                removeStack();
            }

            if (empty && horizontalPosition < 16 || !empty && horizontalPosition < 16 && conveyorBlockEntity.getPosition() > 4) {
                setHorizontalPosition(horizontalPosition + 1);
            } else if (horizontalPosition > prevHorizontalPosition) {
                prevHorizontalPosition = horizontalPosition;
            }
        } else {
            if (horizontalPosition > 0) {
                setHorizontalPosition(horizontalPosition - 1);
            }

            if (position > 0) {
                setPosition(position - 1);
            }
        }
    }

    @Override
    public boolean hasDown() {
        return down;
    }

    @Override
    public void setDown(boolean down) {
        this.down = down;
        markDirty();
    }

    public void advancePosition(BlockPos pos, int distance) {
        DownVerticalConveyorBlockEntity conveyorBlockEntity = (DownVerticalConveyorBlockEntity) getWorld().getBlockEntity(pos);
        boolean empty = conveyorBlockEntity.isEmpty();

        if (!getWorld().isClient() && position >= distance && conveyorBlockEntity.isEmpty()) {
            conveyorBlockEntity.setStack(getStack());
            removeStack();
        }

        if (position < 0 + (distance - 16)) {
            setPosition(position + 1);
        } else if (empty && position < distance || !empty && position < distance && position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(position + 1);
        } else {
            prevPosition = position;
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
        down = compoundTag.getBoolean("down_vertical");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.putBoolean("down_vertical", down);
        return super.toTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
