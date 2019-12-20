package com.zundrel.conveyance.mixin;

import net.minecraft.entity.EntityContextImpl;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityContextImpl.class)
public interface EntityContextImplAccess {
    @Accessor
    Item getHeldItem();
}