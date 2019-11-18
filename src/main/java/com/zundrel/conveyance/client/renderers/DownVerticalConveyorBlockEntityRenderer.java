package com.zundrel.conveyance.client.renderers;

import com.zundrel.conveyance.api.ConveyorType;
import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.api.IConveyorRenderer;
import com.zundrel.conveyance.common.blocks.conveyors.ConveyorProperties;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class DownVerticalConveyorBlockEntityRenderer extends BlockEntityRenderer<DownVerticalConveyorBlockEntity> implements IConveyorRenderer<DownVerticalConveyorBlockEntity> {
    public DownVerticalConveyorBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(DownVerticalConveyorBlockEntity blockEntity, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int i1) {
        int speed = ((IConveyor) blockEntity.getCachedState().getBlock()).getSpeed();
        ConveyorType type = ((IConveyor) blockEntity.getCachedState().getBlock()).getType();
        boolean conveyor = blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR);
        boolean front = blockEntity.getCachedState().get(ConveyorProperties.FRONT);

        if (conveyor && blockEntity.isEmpty()) {
            matrixStack.push();
            renderSupport(blockEntity, type, -1, 16, 0, matrixStack, vertexConsumerProvider);
            matrixStack.pop();
        } else if (conveyor && !front && !blockEntity.isEmpty() && blockEntity.getPosition() > speed) {
            float position = (blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks);

            matrixStack.push();
            matrixStack.translate(0, 0, (position / speed) - 2);
            renderSupport(blockEntity, type, -1, 16, 0, matrixStack, vertexConsumerProvider);
            matrixStack.pop();
        } else if (conveyor && front && !blockEntity.isEmpty() && blockEntity.getHorizontalPosition() > 0) {
            float horizontalPosition = (blockEntity.getRenderAttachmentData()[3] + (blockEntity.getRenderAttachmentData()[2] - blockEntity.getRenderAttachmentData()[3]) * partialTicks);

            matrixStack.push();
            matrixStack.translate(0, 0, (horizontalPosition / speed) - 1);
            renderSupport(blockEntity, type, -1, 16, 0, matrixStack, vertexConsumerProvider);
            matrixStack.pop();
        }

        if (!blockEntity.getWorld().getBlockState(blockEntity.getPos()).isAir() && !blockEntity.isEmpty()) {
            ItemStack stack = blockEntity.getStack();
            float position = -(blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks);
            float horizontalPosition = (blockEntity.getRenderAttachmentData()[3] + (blockEntity.getRenderAttachmentData()[2] - blockEntity.getRenderAttachmentData()[3]) * partialTicks);

            if (blockEntity.getPosition() < 16 && !front) {
                renderSupport(blockEntity, type, position, speed, horizontalPosition, matrixStack, vertexConsumerProvider);
            }

            renderItem(blockEntity, stack, position, speed, horizontalPosition, type, matrixStack, vertexConsumerProvider);
        }
    }
}
