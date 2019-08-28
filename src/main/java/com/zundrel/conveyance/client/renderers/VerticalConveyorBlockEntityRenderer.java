package com.zundrel.conveyance.client.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.zundrel.conveyance.api.IConveyorRenderer;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class VerticalConveyorBlockEntityRenderer extends BlockEntityRenderer<VerticalConveyorBlockEntity> implements IConveyorRenderer<VerticalConveyorBlockEntity> {
    @Override
    public void render(VerticalConveyorBlockEntity blockEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!blockEntity.getWorld().isAir(blockEntity.getPos()) && !blockEntity.isInvEmpty()) {
            ItemStack stack = blockEntity.getInvStack(0);
            Direction direction = blockEntity.getCachedState().get(HorizontalFacingBlock.FACING);

            GlStateManager.pushMatrix();

            setProperties(blockEntity, x, y, z, direction);

            float position = blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks;
            float horizontalPosition = blockEntity.getRenderAttachmentData()[3] + (blockEntity.getRenderAttachmentData()[2] - blockEntity.getRenderAttachmentData()[3]) * partialTicks;

            GlStateManager.translated((horizontalPosition / 8F), (position / 16F), 0);

            renderSupport(blockEntity);

            renderItem(stack);

            GlStateManager.popMatrix();
        }
    }
}
