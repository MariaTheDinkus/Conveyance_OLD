package com.zundrel.conveyance.client.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.api.IConveyorRenderer;
import com.zundrel.conveyance.common.blocks.ConveyorProperties;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class DownVerticalConveyorBlockEntityRenderer extends BlockEntityRenderer<DownVerticalConveyorBlockEntity> implements IConveyorRenderer<DownVerticalConveyorBlockEntity> {
    @Override
    public void render(DownVerticalConveyorBlockEntity blockEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        Direction direction = blockEntity.getCachedState().get(HorizontalFacingBlock.FACING).getOpposite();
        boolean conveyor = blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR);
        boolean front = blockEntity.getCachedState().get(ConveyorProperties.FRONT);

        if (conveyor && blockEntity.isInvEmpty()) {
            GlStateManager.pushMatrix();
            setProperties(blockEntity, x, y, z, direction);
            GlStateManager.translated(0, (15F / 16F), 0);
            renderSupport(blockEntity);
            GlStateManager.popMatrix();
        } else if (conveyor && !front && !blockEntity.isInvEmpty() && blockEntity.getPosition() > 16) {
            GlStateManager.pushMatrix();
            setProperties(blockEntity, x, y, z, direction);

            float offset = -32;
            float speed = 1;
            float supportPosition = ((blockEntity.getRenderAttachmentData()[1] / speed) + offset) + (((blockEntity.getRenderAttachmentData()[0] / speed) + offset) - ((blockEntity.getRenderAttachmentData()[1] / speed) + offset)) * partialTicks;

            GlStateManager.translated(((supportPosition) / 32F), (15F / 16F), 0);
            renderSupport(blockEntity);
            GlStateManager.popMatrix();
        } else if (conveyor && front && !blockEntity.isInvEmpty() && blockEntity.getHorizontalPosition() > 0) {
            GlStateManager.pushMatrix();
            setProperties(blockEntity, x, y, z, direction);

            float offset = -16;
            float speed = 1;
            float supportPosition = ((blockEntity.getRenderAttachmentData()[3] / speed) + offset) + (((blockEntity.getRenderAttachmentData()[2] / speed) + offset) - ((blockEntity.getRenderAttachmentData()[3] / speed) + offset)) * partialTicks;

            GlStateManager.translated(((supportPosition) / 32F), (15F / 16F), 0);
            renderSupport(blockEntity);
            GlStateManager.popMatrix();
        }

        if (!blockEntity.getWorld().isAir(blockEntity.getPos()) && !blockEntity.isInvEmpty()) {
            ItemStack stack = blockEntity.getInvStack(0);

            GlStateManager.pushMatrix();

            setProperties(blockEntity, x, y, z, direction);

            float position = -(blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks);
            float horizontalPosition = (blockEntity.getRenderAttachmentData()[3] + (blockEntity.getRenderAttachmentData()[2] - blockEntity.getRenderAttachmentData()[3]) * partialTicks);
            float verticalOffset = conveyor ? 1 : 0;

            GlStateManager.translated((horizontalPosition / 16F), (position / 16F) + verticalOffset, 0);

            //Conveyance.LOGGER.info(position);

            renderSupport(blockEntity);

            renderItem(stack);

            GlStateManager.popMatrix();
        }
    }
}
