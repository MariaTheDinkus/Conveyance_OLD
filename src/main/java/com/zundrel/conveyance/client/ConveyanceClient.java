package com.zundrel.conveyance.client;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.client.renderers.ConveyorBlockEntityRenderer;
import com.zundrel.conveyance.client.renderers.DownVerticalConveyorBlockEntityRenderer;
import com.zundrel.conveyance.client.renderers.VerticalConveyorBlockEntityRenderer;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class ConveyanceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ConveyorBlockEntity.class, new ConveyorBlockEntityRenderer());
        BlockEntityRendererRegistry.INSTANCE.register(VerticalConveyorBlockEntity.class, new VerticalConveyorBlockEntityRenderer());
        BlockEntityRendererRegistry.INSTANCE.register(DownVerticalConveyorBlockEntity.class, new DownVerticalConveyorBlockEntityRenderer());

        ModelLoadingRegistry.INSTANCE.registerAppender((resourceManager, consumer) -> {
            consumer.accept(new ModelIdentifier(new Identifier(Conveyance.MODID, "conveyor_supports"), ""));
        });
    }
}
