package com.zundrel.conveyance.common.blocks.entities;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.ConveyorProperties;
import com.zundrel.conveyance.common.registries.ModBlockEntities;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DownVerticalConveyorBlockEntity extends ConveyorBlockEntity {
    protected boolean down = false;
    protected int horizontalPosition;
    protected int prevHorizontalPosition;

    public DownVerticalConveyorBlockEntity() {
        super(ModBlockEntities.DOWN_VERTICAL_CONVEYOR);
    }

    @Override
    public void tick() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        boolean front = getCachedState().get(ConveyorProperties.FRONT);
        boolean conveyor = getCachedState().get(ConveyorProperties.CONVEYOR);

        if (!isInvEmpty() && conveyor && down && getWorld().getBlockEntity(getPos().down()) instanceof DownVerticalConveyorBlockEntity) {
            advancePosition(getPos().down(), 32);
        } else if (!isInvEmpty() && !conveyor && down && getWorld().getBlockEntity(getPos().down()) instanceof DownVerticalConveyorBlockEntity) {
            advancePosition(getPos().down(), 16);
        } else if (!isInvEmpty() && conveyor && front && getWorld().getBlockEntity(getPos().offset(direction.getOpposite())) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction.getOpposite()));
            boolean empty = conveyorBlockEntity.isInvEmpty();

            if (!getWorld().isClient() && position >= 16 && horizontalPosition >= 16 && conveyorBlockEntity.isInvEmpty()) {
                conveyorBlockEntity.setInvStack(0, getInvStack(0));
                removeInvStack(0);
            }

            if (empty && position < 16  || !empty && position < 16 && conveyorBlockEntity.getPosition() > 4) {
                setPosition(position + 1);
            } else if (position > prevPosition) {
                prevPosition = position;
            }

            if (empty && position >= 16 && horizontalPosition < 16 || !empty && position >= 16 && horizontalPosition < 16 && conveyorBlockEntity.getPosition() > 4) {
                Conveyance.LOGGER.info("HELP");
                setHorizontalPosition(horizontalPosition + 1);
            } else if (horizontalPosition > prevHorizontalPosition) {
                prevHorizontalPosition = horizontalPosition;
            }
        } else if (!isInvEmpty() && !conveyor && front && getWorld().getBlockEntity(getPos().offset(direction.getOpposite())) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction.getOpposite()));
            boolean empty = conveyorBlockEntity.isInvEmpty();

            if (!getWorld().isClient() && horizontalPosition >= 16 && conveyorBlockEntity.isInvEmpty()) {
                conveyorBlockEntity.setInvStack(0, getInvStack(0));
                removeInvStack(0);
            }

            if (empty && horizontalPosition < 16 || !empty && horizontalPosition < 16 && conveyorBlockEntity.getPosition() > 4) {
                setHorizontalPosition(horizontalPosition + 1);
            } else if (horizontalPosition > prevHorizontalPosition) {
                prevHorizontalPosition = horizontalPosition;
            }
        } else {
            if (horizontalPosition > 0) {
                setHorizontalPosition(horizontalPosition - 1);
            } else if (position > 0) {
                setPosition(position - 1);
            }
        }
    }

    public boolean hasDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
        markDirty();
    }

    public void advancePosition(BlockPos pos, int distance) {
        DownVerticalConveyorBlockEntity conveyorBlockEntity = (DownVerticalConveyorBlockEntity) getWorld().getBlockEntity(pos);
        boolean empty = conveyorBlockEntity.isInvEmpty();

        if (!getWorld().isClient() && position >= distance && conveyorBlockEntity.isInvEmpty()) {
            conveyorBlockEntity.setInvStack(0, getInvStack(0));
            removeInvStack(0);
        }

        if (position < 0 + (distance - 16)) {
            setPosition(position + 1);
        } else if (empty && position < distance || !empty && position < distance && position + 4 < conveyorBlockEntity.getPosition() && conveyorBlockEntity.getPosition() > 4) {
            setPosition(position + 1);
        } else {
            prevPosition = position;
        }

        Conveyance.LOGGER.info(position);
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
    public void fromTag(CompoundTag compoundTag_1) {
        super.fromTag(compoundTag_1);
        items.clear();
        Inventories.fromTag(compoundTag_1, items);
        down = compoundTag_1.getBoolean("down_vertical");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag_1) {
        Inventories.toTag(compoundTag_1, items);
        compoundTag_1.putBoolean("down_vertical", down);
        return super.toTag(compoundTag_1);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
