package com.zundrel.conveyance.client.renderers;

import com.zundrel.conveyance.api.Conveyor;
import com.zundrel.conveyance.api.ConveyorRenderer;
import com.zundrel.conveyance.api.ConveyorType;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ConveyorBlockEntityRenderer extends BlockEntityRenderer<ConveyorBlockEntity> implements ConveyorRenderer<ConveyorBlockEntity> {
    public ConveyorBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(ConveyorBlockEntity blockEntity, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int i1) {
        int speed = ((Conveyor) blockEntity.getCachedState().getBlock()).getSpeed();
        ConveyorType type = ((Conveyor) blockEntity.getCachedState().getBlock()).getType();

        if (!blockEntity.getWorld().getBlockState(blockEntity.getPos()).isAir() && !blockEntity.isEmpty() && !blockEntity.hasBeenRemoved()) {
            ItemStack stack = blockEntity.getStack();

            float position = blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks;

            renderItem(blockEntity, stack, position, speed, 0, type, matrixStack, vertexConsumerProvider);
        }
    }
}
