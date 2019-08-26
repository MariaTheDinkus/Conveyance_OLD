package com.zundrel.conveyance.client.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.zundrel.conveyance.api.IConveyorRenderer;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.minecraft.block.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class ConveyorBlockEntityRenderer extends BlockEntityRenderer<ConveyorBlockEntity> implements IConveyorRenderer<ConveyorBlockEntity> {
    @Override
    public void render(ConveyorBlockEntity blockEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!blockEntity.getWorld().isAir(blockEntity.getPos()) && !blockEntity.isInvEmpty()) {
            ItemStack stack = blockEntity.getInvStack(0);
            Direction direction = blockEntity.getWorld().getBlockState(blockEntity.getPos()).get(HorizontalFacingBlock.FACING);

            GlStateManager.pushMatrix();

            setProperties(blockEntity, x, y, z, direction);

            float position = blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks;

            GlStateManager.translated((position / 16F), 0, 0);

            renderItem(stack);

            GlStateManager.popMatrix();
        }
    }
}
