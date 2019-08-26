package com.zundrel.conveyance.api;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.zundrel.conveyance.common.blocks.ConveyorProperties;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public interface IConveyorRenderer<T extends ConveyorBlockEntity> {
    default void setProperties(T blockEntity, double x, double y, double z, Direction direction) {
        int light = blockEntity.getWorld().getLightmapIndex(blockEntity.getPos(), 0);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) (light & 0xFFFF), (float) ((light >> 16) & 0xFFFF));

        GlStateManager.translated(x + 0.5, y + (4F / 16F), z + 0.5);

        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            GlStateManager.rotatef(direction.asRotation() - 90, 0, 1, 0);
        } else {
            GlStateManager.rotatef(direction.getOpposite().asRotation() - 90, 0, 1, 0);
        }
    }

    default void renderSupport(T blockEntity) {
        GlStateManager.pushMatrix();

        GlStateManager.scaled(1.5, 1.5, 1.5);

        GlStateManager.translated(0, 2.49F / 16F, 0);

        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Blocks.IRON_TRAPDOOR), ModelTransformation.Type.FIXED);

        GlStateManager.popMatrix();

        if (blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) && blockEntity.getPosition() == 15) {
            GlStateManager.translated(0, 1F / 16F, 0);
        }
    }

    default void renderItem(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) stack.getItem();
            Block block = blockItem.getBlock();

            MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

            if (block instanceof SkullBlock) {
                GlStateManager.translated(0, 4F / 16F, 0);
                GlStateManager.rotatef(90, 0, 1, 0);
                GlStateManager.rotated(90, 1, 0, 0);

                if (block == Blocks.DRAGON_HEAD)
                    GlStateManager.scaled(0.6, 0.6, 0.6);

                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
            } else if (blockItem instanceof TallBlockItem) {
                GlStateManager.scaled(0.25, 0.25, 0.25);
                GlStateManager.translated(-1 + (1.5F / 16F), 0, 0.5);

                MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState(), 1);

                GlStateManager.translated(0, 1, 0);
                GlStateManager.rotated(-90, 0, 1, 0);

                MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState().with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 1);
            } else if (block instanceof GrassBlock) {
                GlStateManager.translated(0, 4F / 16F, 0);

                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
            } else {
                GlStateManager.scaled(0.5, 0.5, 0.5);
                GlStateManager.translated(-0.5, 0, 0.5);

                if (block instanceof BedBlock) {
                    GlStateManager.translated(0.5, 0, 0);
                }

                if (block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT) {
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableRescaleNormal();
                    GlStateManager.alphaFunc(516, 0.1F);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                }

                BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(block.getDefaultState());
                MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(model, block.getDefaultState(), 1, true);

                if (block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT) {
                    GlStateManager.disableRescaleNormal();
                    GlStateManager.disableBlend();
                }
            }
        } else {
            Item item = stack.getItem();

            GlStateManager.translated(0, (0.5F / 16F), 0);
            GlStateManager.rotatef(90, 1, 0, 0);
            GlStateManager.scaled(0.6F, 0.6F, 0.6F);

            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
        }
    }
}
