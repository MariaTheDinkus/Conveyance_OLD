package com.zundrel.conveyance.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWrenchable {
    void onWrenched(World world, BlockState state, BlockPos pos, PlayerEntity player);
}
