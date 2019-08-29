package com.zundrel.conveyance.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zundrel.conveyance.api.IConveyorRenderer;
import com.zundrel.conveyance.common.blocks.ConveyorProperties;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
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

        if (conveyor && blockEntity.isEmpty()) {
            RenderSystem.pushMatrix();
            setProperties(blockEntity, x, y, z, direction);
            RenderSystem.translated(0, (15F / 16F), 0);
            renderSupport(blockEntity);
            RenderSystem.popMatrix();
        } else if (conveyor && !front && !blockEntity.isEmpty() && blockEntity.getPosition() > 16) {
            RenderSystem.pushMatrix();
            setProperties(blockEntity, x, y, z, direction);

            float offset = -32;
            float speed = 1;
            float supportPosition = ((blockEntity.getRenderAttachmentData()[1] / speed) + offset) + (((blockEntity.getRenderAttachmentData()[0] / speed) + offset) - ((blockEntity.getRenderAttachmentData()[1] / speed) + offset)) * partialTicks;

            RenderSystem.translated(((supportPosition) / 32F), (15F / 16F), 0);
            renderSupport(blockEntity);
            RenderSystem.popMatrix();
        } else if (conveyor && front && !blockEntity.isEmpty() && blockEntity.getHorizontalPosition() > 0) {
            RenderSystem.pushMatrix();
            setProperties(blockEntity, x, y, z, direction);

            float offset = -16;
            float speed = 1;
            float supportPosition = ((blockEntity.getRenderAttachmentData()[3] / speed) + offset) + (((blockEntity.getRenderAttachmentData()[2] / speed) + offset) - ((blockEntity.getRenderAttachmentData()[3] / speed) + offset)) * partialTicks;

            RenderSystem.translated(((supportPosition) / 32F), (15F / 16F), 0);
            renderSupport(blockEntity);
            RenderSystem.popMatrix();
        }

        if (!blockEntity.getWorld().getBlockState(blockEntity.getPos()).isAir() && !blockEntity.isEmpty()) {
            ItemStack stack = blockEntity.getStack();

            RenderSystem.pushMatrix();

            setProperties(blockEntity, x, y, z, direction);

            float position = -(blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks);
            float horizontalPosition = (blockEntity.getRenderAttachmentData()[3] + (blockEntity.getRenderAttachmentData()[2] - blockEntity.getRenderAttachmentData()[3]) * partialTicks);
            float verticalOffset = conveyor ? 1 : 0;

            RenderSystem.translated((horizontalPosition / 16F), (position / 16F) + verticalOffset, 0);

            renderSupport(blockEntity);

            renderItem(stack);

            RenderSystem.popMatrix();
        }
    }
}
