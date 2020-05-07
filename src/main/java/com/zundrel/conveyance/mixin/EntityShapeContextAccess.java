package com.zundrel.conveyance.mixin;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityShapeContext.class)
public interface EntityShapeContextAccess {
    @Accessor
    Item getHeldItem();
}