package com.zundrel.conveyance.client;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.client.renderers.*;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import com.zundrel.conveyance.common.registries.ConveyanceBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class ConveyanceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerAppender((resourceManager, consumer) -> {
            consumer.accept(new ModelIdentifier(new Identifier(Conveyance.MODID, "conveyor_supports"), ""));
        });

        ConveyanceBlocks.registerRenderLayers();

		BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.ALTERNATOR, (blockEntityRenderDispatcher -> new DoubleMachineEntityRenderer(BlockEntityRenderDispatcher.INSTANCE)));
		BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.SPLITTER, (blockEntityRenderDispatcher -> new DoubleMachineEntityRenderer(BlockEntityRenderDispatcher.INSTANCE)));
		BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.INSERTER, (blockEntityRenderDispatcher -> new InserterBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE)));

        BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.CONVEYOR, (blockEntityRenderDispatcher -> new ConveyorBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE)));
        BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.VERTICAL_CONVEYOR, (blockEntityRenderDispatcher -> new VerticalConveyorBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE)));
        BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.DOWN_VERTICAL_CONVEYOR, (blockEntityRenderDispatcher -> new DownVerticalConveyorBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE)));
    }
}
