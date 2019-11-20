package com.zundrel.conveyance.client;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.client.renderers.ConveyorBlockEntityRenderer;
import com.zundrel.conveyance.client.renderers.DownVerticalConveyorBlockEntityRenderer;
import com.zundrel.conveyance.client.renderers.VerticalConveyorBlockEntityRenderer;
import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import com.zundrel.conveyance.common.registries.ConveyanceBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class ConveyanceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.CONVEYOR, new ConveyorBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE));
        BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.VERTICAL_CONVEYOR, new VerticalConveyorBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE));
        BlockEntityRendererRegistry.INSTANCE.register(ConveyanceBlockEntities.DOWN_VERTICAL_CONVEYOR, new DownVerticalConveyorBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE));

        ModelLoadingRegistry.INSTANCE.registerAppender((resourceManager, consumer) -> {
            consumer.accept(new ModelIdentifier(new Identifier(Conveyance.MODID, "conveyor_supports"), ""));
        });

        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.VERTICAL_CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.DOWN_VERTICAL_CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.FAST_CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.VERTICAL_FAST_CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.DOWN_VERTICAL_FAST_CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.EXPRESS_CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.VERTICAL_EXPRESS_CONVEYOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.DOWN_VERTICAL_EXPRESS_CONVEYOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.ALTERNATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConveyanceBlocks.SPLITTER, RenderLayer.getCutout());
    }
}
