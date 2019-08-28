package com.zundrel.conveyance.common.items;

import com.zundrel.conveyance.api.IWrenchable;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class WrenchItem extends Item {
    public WrenchItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext_1) {
        World world = itemUsageContext_1.getWorld();
        BlockState state = world.getBlockState(itemUsageContext_1.getBlockPos());

        if (state.getBlock() instanceof IWrenchable) {
            ((IWrenchable) state.getBlock()).onWrenched(world, state, itemUsageContext_1.getBlockPos(), itemUsageContext_1.getPlayer());

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
