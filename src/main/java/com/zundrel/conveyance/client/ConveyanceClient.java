package com.zundrel.conveyance.client;

import com.zundrel.conveyance.client.renderers.ConveyorBlockEntityRenderer;
import com.zundrel.conveyance.client.renderers.VerticalConveyorBlockEntityRenderer;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

public class ConveyanceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ConveyorBlockEntity.class, new ConveyorBlockEntityRenderer());
        BlockEntityRendererRegistry.INSTANCE.register(VerticalConveyorBlockEntity.class, new VerticalConveyorBlockEntityRenderer());
    }
}
