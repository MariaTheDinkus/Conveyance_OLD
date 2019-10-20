package com.zundrel.conveyance.api;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.conveyors.ConveyorProperties;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Random;

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

        if (blockEntity instanceof DownVerticalConveyorBlockEntity)
            GlStateManager.rotated(-90, 0, 1, 0);
        else
            GlStateManager.rotated(90, 0, 1, 0);

        GlStateManager.translated(-0.5, -1.001, -0.5);

        if (!(blockEntity instanceof DownVerticalConveyorBlockEntity) && blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) && blockEntity.getPosition() == 16)
            GlStateManager.translated(0, -1F / 16F, 0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier(new Identifier(Conveyance.MODID, "conveyor_supports"),  ""));

        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(model, 1, 1, 1, 1);

        GlStateManager.popMatrix();
    }

    default void renderItem(ItemStack stack) {
        Random random = new Random();

        int int_1 = 1;
        if (stack.getCount() > 48) {
            int_1 = 5;
        } else if (stack.getCount() > 32) {
            int_1 = 4;
        } else if (stack.getCount() > 16) {
            int_1 = 3;
        } else if (stack.getCount() > 1) {
            int_1 = 2;
        }

        int seed = stack.isEmpty() ? 187 : Item.getRawId(stack.getItem()) + stack.getDamage();
        random.setSeed(seed);

        if (stack.getItem() instanceof BlockItem && stack.getItem() != Items.REDSTONE) {
            BlockItem blockItem = (BlockItem) stack.getItem();
            Block block = blockItem.getBlock();

            MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

            if (block instanceof SkullBlock) {
                GlStateManager.translated(0, 4F / 16F, 0);
                GlStateManager.rotatef(90, 0, 1, 0);

                if (block == Blocks.DRAGON_HEAD)
                    GlStateManager.scaled(0.6, 0.6, 0.6);

                for (int i = 0; i < int_1; i++) {
                    GlStateManager.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.rotated(random.nextInt(10), 0, 1, 0);
                        GlStateManager.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
                    GlStateManager.popMatrix();
                }
            } else if (blockItem instanceof TallBlockItem) {
                GlStateManager.scaled(0.25, 0.25, 0.25);
                GlStateManager.translated(-1 + (1.5F / 16F), 0, 0.5);

                for (int i = 0; i < int_1; i++) {
                    GlStateManager.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.rotated(random.nextInt(10), 0, 1, 0);
                        GlStateManager.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState(), 1);

                    GlStateManager.translated(0, 1, 0);
                    GlStateManager.rotated(-90, 0, 1, 0);

                    MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState().with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 1);
                    GlStateManager.popMatrix();
                }
            } else if (block instanceof GrassBlock || block instanceof BannerBlock || block instanceof SignBlock) {
                GlStateManager.translated(0, 4F / 16F, 0);

                if (block instanceof BannerBlock || block instanceof SignBlock) {
                    GlStateManager.scaled(0.6, 0.6, 0.6);
                    GlStateManager.rotatef(90, 0, 1, 0);
                }

                for (int i = 0; i < int_1; i++) {
                    GlStateManager.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.rotated(random.nextInt(10), 0, 1, 0);
                        GlStateManager.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
                    GlStateManager.popMatrix();
                }
            } else {
                GlStateManager.scaled(0.5, 0.5, 0.5);

                if (block instanceof ChestBlock || block instanceof EnderChestBlock)
                    GlStateManager.rotated(180, 0, 1, 0);

                GlStateManager.translated(-0.5, 0, 0.5);

                if (block instanceof BedBlock)
                    GlStateManager.translated(0.5, 0, 0);

                if (block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT) {
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableRescaleNormal();
                    GlStateManager.alphaFunc(516, 0.1F);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
                }

                for (int i = 0; i < int_1; i++) {
                    GlStateManager.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.rotated(random.nextInt(10), 0, 1, 0);
                        GlStateManager.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState(), 1);
                    GlStateManager.popMatrix();
                }

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

            for (int i = 0; i < int_1; i++) {
                GlStateManager.pushMatrix();
                if (i > 0) {
                    float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float y = (random.nextFloat() * 2.0F) * 0.15F;
                    float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.rotated(random.nextInt(10), 0, 1, 0);
                    GlStateManager.translated(x * 2, y * 0.5, z * 2);
                }
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
                GlStateManager.popMatrix();
            }
        }
    }
}
