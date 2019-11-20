package com.zundrel.conveyance.common.blocks.entities;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;
import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.api.IConveyorMachine;
import com.zundrel.conveyance.common.blocks.conveyors.ConveyorBlock;
import com.zundrel.conveyance.common.inventory.ConveyorExtractable;
import com.zundrel.conveyance.common.inventory.ConveyorInsertable;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ConveyorBlockEntity extends BlockEntity implements AttributeProvider, BlockEntityClientSerializable, RenderAttachmentBlockEntity, Tickable {
    private ItemStack stack = ItemStack.EMPTY;
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
            advancePosition(getPos().offset(direction).down(1));
        } else if (!isEmpty() && position > 0) {
            setPosition(Math.max(0, position - 4));
        } else if (getWorld().getTime() % speed == 0) {
            if (!isEmpty()) {
                ItemInsertable insertable = ItemAttributes.INSERTABLE.get(world, getPos().offset(direction), SearchOptions.inDirection(direction));

                ItemStack stack = insertable.attemptInsertion(this.stack, Simulation.ACTION);
                if (stack.isEmpty() || stack.getCount() != getStack().getCount()) {
                    setStack(stack);
                }
            }

            if (isEmpty()) {
                BlockState state = this.getCachedState();
                if (state.getBlock() instanceof ConveyorBlock) {
                    Direction facing = state.get(Properties.HORIZONTAL_FACING);

                    BlockPos behind = pos.offset(facing.getOpposite());
                    BlockState behindState = world.getBlockState(behind);
                    if (!(behindState.getBlock() instanceof IConveyor)) { //Don't pull. We'll get a push
                        ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(world, behind, SearchOptions.inDirection(facing.getOpposite()));
                        ItemStack stack = extractable.attemptAnyExtraction(64, Simulation.ACTION);
                        if (!stack.isEmpty()) {
                            setStack(stack);
                        }
                    }
                }

                if (state.getBlock() instanceof ConveyorBlock) {
                    BlockPos top = pos.offset(Direction.UP);
                    BlockState topState = world.getBlockState(top);
                    if (topState.getBlock() instanceof HopperBlock) { //Don't pull. We'll get a push
                        ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(world, top, SearchOptions.inDirection(Direction.UP));
                        ItemStack stack = extractable.attemptAnyExtraction(64, Simulation.ACTION);
                        if (!stack.isEmpty()) {
                            setStack(stack);
                        }
                    }
                }
            }
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

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        markDirty();
    }

    public void removeStack() {
        this.stack = ItemStack.EMPTY;
        markDirty();
    }

    public void clear() {
        this.stack = ItemStack.EMPTY;
    }

    public boolean isEmpty() {
        return getStack().isEmpty();
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
            ((ServerWorld)world).method_14178().markForUpdate(pos);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sync();
    }

    @Override
    public void addAllAttributes(World world, BlockPos blockPos, BlockState blockState, AttributeList<?> attributeList) {
        Direction dir = attributeList.getSearchDirection();
        if (dir==null) return; //We're not offering anything to omnidirectional searches
        if (dir==Direction.UP) {
            attributeList.offer(new ConveyorInsertable(this));
        } else if (dir==Direction.DOWN) {
            attributeList.offer(new ConveyorExtractable(this));
        } else {
            if (blockState.getBlock() instanceof ConveyorBlock) {
                Direction facing = blockState.get(Properties.HORIZONTAL_FACING);

                if (dir==facing) {
                    attributeList.offer(EmptyItemExtractable.SUPPLIER); //Don't call us, we'll call you.
                } else if (dir==facing.getOpposite()) {
                    attributeList.offer(new ConveyorInsertable(this));
                } else {



                }
            }
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        clear();
        stack = ItemStack.fromTag(compoundTag.getCompound("stack"));
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
        compoundTag.put("stack", stack.toTag(new CompoundTag()));
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
