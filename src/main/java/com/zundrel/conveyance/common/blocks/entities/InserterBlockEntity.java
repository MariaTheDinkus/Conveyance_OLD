package com.zundrel.conveyance.common.blocks.entities;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;
import com.zundrel.conveyance.common.blocks.conveyors.ConveyorBlock;
import com.zundrel.conveyance.common.inventory.InserterExtractable;
import com.zundrel.conveyance.common.inventory.InserterInsertable;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
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

public class InserterBlockEntity extends BlockEntity implements AttributeProvider, BlockEntityClientSerializable, Tickable {
    private ItemStack stack = ItemStack.EMPTY;

    public InserterBlockEntity() {
        super(ConveyanceBlockEntities.INSERTER);
    }

    public InserterBlockEntity(BlockEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
        Direction direction = getCachedState().get(HorizontalFacingBlock.FACING);

        if (!getWorld().isClient() && getWorld().getTime() % 15 == 0) {
            if (!isEmpty()) {
                ItemInsertable insertable = ItemAttributes.INSERTABLE.get(world, getPos().offset(direction), SearchOptions.inDirection(direction));

                ItemStack stack = insertable.attemptInsertion(this.stack, Simulation.ACTION);
                if (stack.isEmpty() || stack.getCount()!=getStack().getCount()) {
                    setStack(stack);
                }
            }

            if (isEmpty()) {
                BlockState state = this.getCachedState();
                Direction facing = state.get(Properties.HORIZONTAL_FACING);

                BlockPos behind = pos.offset(facing.getOpposite());
                ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(world, behind, SearchOptions.inDirection(facing.getOpposite()));
                ItemStack stack = extractable.attemptAnyExtraction(64, Simulation.ACTION);
                if (!stack.isEmpty()) {
                    setStack(stack);
                }
            }
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
    public void addAllAttributes(World world, BlockPos blockPos, BlockState blockState, AttributeList<?> attributeList) {
        Direction dir = attributeList.getSearchDirection();
        if (dir==null) return; //We're not offering anything to omnidirectional searches
        if (dir==Direction.UP) {
            attributeList.offer(new InserterInsertable(this));
        } else if (dir==Direction.DOWN) {
            attributeList.offer(new InserterExtractable(this));
        } else {
            if (blockState.getBlock() instanceof ConveyorBlock) {
                Direction facing = blockState.get(Properties.HORIZONTAL_FACING);

                if (dir==facing) {
                    attributeList.offer(EmptyItemExtractable.SUPPLIER); //Don't call us, we'll call you.
                } else if (dir==facing.getOpposite()) {
                    attributeList.offer(new InserterInsertable(this));
                }
            }
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        clear();
        stack = ItemStack.fromTag(compoundTag.getCompound("stack"));
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.put("stack", stack.toTag(new CompoundTag()));
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
