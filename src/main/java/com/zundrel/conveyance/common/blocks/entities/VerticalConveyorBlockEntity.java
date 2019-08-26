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
    protected int horizontalPosition;
    protected int prevHorizontalPosition;

    public VerticalConveyorBlockEntity() {
        super(ModBlockEntities.VERTICAL_CONVEYOR);
    }

    @Override
    public void tick() {
        tickConveyor();
    }

    private void tickConveyor() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
        boolean up = getCachedState().get(ConveyorProperties.UP);
        boolean conveyor = getCachedState().get(ConveyorProperties.CONVEYOR);

        if (!isInvEmpty() && up && getWorld().getBlockEntity(getPos().up()) instanceof VerticalConveyorBlockEntity) {
            advancePosition(getPos().up());
        } else if (!conveyor && position > 0) {
            setPosition(0);
        }

        if (!isInvEmpty() && conveyor && getWorld().getBlockEntity(getPos().offset(direction).up()) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(getPos().offset(direction).up());
            ItemStack stack = conveyorBlockEntity.getInvStack(0);
            boolean empty = conveyorBlockEntity.isInvEmpty();

            if (!getWorld().isClient() && position >= 15 && horizontalPosition >= 8 && conveyorBlockEntity.isInvEmpty()) {
                conveyorBlockEntity.setInvStack(0, getInvStack(0));
                removeInvStack(0);
            }

            if (empty && position < 15 || !empty && position < 15 && conveyorBlockEntity.getPosition() > 4) {
                setPosition(position + 1);
            } else {
                prevPosition = position;
            }

            if (empty && horizontalPosition < 8 && position >= 15 || !empty && horizontalPosition < 8 && position >= 15 && conveyorBlockEntity.getPosition() > 4) {
                setHorizontalPosition(horizontalPosition + 1);
            } else {
                prevHorizontalPosition = horizontalPosition;
            }
        } else if (!up && position > 0) {
            setPosition(0);
        }

        if (isInvEmpty() && position > 0) {
            setPosition(0);
        }

        if (isInvEmpty() && horizontalPosition > 0 || !conveyor && horizontalPosition > 0) {
            setHorizontalPosition(0);
        }
    }

//    private void tickDown() {
//        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);
//        boolean front = getCachedState().get(ConveyorProperties.FRONT);
//        boolean conveyor = getCachedState().get(ConveyorProperties.CONVEYOR);
//
//        if (!isInvEmpty() && !conveyor && getWorld().getBlockEntity(getPos().down()) instanceof VerticalConveyorBlockEntity && getWorld().getBlockState(getPos().down()).get(ConveyorProperties.DOWN)) {
//            horizontalPosition = 8;
//            prevHorizontalPosition = 8;
//
//            advancePosition(getPos().down());
//        }
//
//        if (!isInvEmpty() && conveyor && getWorld().getBlockEntity(getPos().down()) instanceof VerticalConveyorBlockEntity && getWorld().getBlockState(getPos().down()).get(ConveyorProperties.DOWN)) {
//            VerticalConveyorBlockEntity conveyorBlockEntity = (VerticalConveyorBlockEntity) getWorld().getBlockEntity(getPos().down());
//            ItemStack stack = conveyorBlockEntity.getInvStack(0);
//            boolean empty = conveyorBlockEntity.isInvEmpty();
//
//            if (!getWorld().isClient() && position >= 16 && horizontalPosition >= 8 && conveyorBlockEntity.isInvEmpty()) {
//                conveyorBlockEntity.setInvStack(0, getInvStack(0));
//                removeInvStack(0);
//            }
//
//            if (horizontalPosition < 8) {
//                setHorizontalPosition(horizontalPosition + 1);
//            } else {
//                prevHorizontalPosition = horizontalPosition;
//            }
//
//            if (empty && position < 16 && horizontalPosition >= 8 || !empty && position < 16 && horizontalPosition >= 8 && conveyorBlockEntity.getPosition() > 4) {
//                setPosition(position + 1);
//            } else {
//                prevPosition = position;
//            }
//        }
//
//        if (!isInvEmpty() && front && getWorld().getBlockEntity(getPos().offset(direction.getOpposite())) instanceof ConveyorBlockEntity) {
//            horizontalPosition = 8;
//            prevHorizontalPosition = 8;
//
//            advancePosition(getPos().offset(direction.getOpposite()));
//        }
//
//        if (isInvEmpty() && position > 0) {
//            horizontalPosition = 8;
//            prevHorizontalPosition = 8;
//
//            setPosition(0);
//        }
//    }

    public void advancePosition(BlockPos pos) {
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) getWorld().getBlockEntity(pos);
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
}
