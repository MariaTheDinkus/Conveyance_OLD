package com.zundrel.conveyance.api;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zundrel.conveyance.common.blocks.ConveyorProperties;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.Random;

@Environment(EnvType.CLIENT)
public interface IConveyorRenderer<T extends ConveyorBlockEntity> {
    default void setProperties(T blockEntity, double x, double y, double z, Direction direction) {
        GuiLighting.enable();

        RenderSystem.translated(x + 0.5, y + (4F / 16F), z + 0.5);

        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            RenderSystem.rotatef(direction.asRotation() - 90, 0, 1, 0);
        } else {
            RenderSystem.rotatef(direction.getOpposite().asRotation() - 90, 0, 1, 0);
        }
    }

    default void renderSupport(T blockEntity) {
        RenderSystem.pushMatrix();

        RenderSystem.scaled(1.5, 1.5, 1.5);

        RenderSystem.translated(0, 2.49F / 16F, 0);

        if (!(blockEntity instanceof DownVerticalConveyorBlockEntity) && blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) && blockEntity.getPosition() == 16)
            RenderSystem.translated(0, -1F / 16F, 0);

        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Blocks.IRON_TRAPDOOR), ModelTransformation.Type.FIXED);

        RenderSystem.popMatrix();
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
                RenderSystem.translated(0, 4F / 16F, 0);
                RenderSystem.rotatef(90, 0, 1, 0);

                if (block == Blocks.DRAGON_HEAD)
                    RenderSystem.scaled(0.6, 0.6, 0.6);

                for (int i = 0; i < int_1; i++) {
                    RenderSystem.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        RenderSystem.rotated(random.nextInt(10), 0, 1, 0);
                        RenderSystem.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
                    RenderSystem.popMatrix();
                }
            } else if (blockItem instanceof TallBlockItem) {
                RenderSystem.scaled(0.25, 0.25, 0.25);
                RenderSystem.translated(-1 + (1.5F / 16F), 0, 0.5);

                for (int i = 0; i < int_1; i++) {
                    RenderSystem.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        RenderSystem.rotated(random.nextInt(10), 0, 1, 0);
                        RenderSystem.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState(), 1);

                    RenderSystem.translated(0, 1, 0);
                    RenderSystem.rotated(-90, 0, 1, 0);

                    MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState().with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 1);
                    RenderSystem.popMatrix();
                }
            } else if (block instanceof GrassBlock || block instanceof BannerBlock || block instanceof SignBlock) {
                RenderSystem.translated(0, 4F / 16F, 0);

                if (block instanceof BannerBlock || block instanceof SignBlock) {
                    RenderSystem.scaled(0.6, 0.6, 0.6);
                    RenderSystem.rotatef(90, 0, 1, 0);
                }

                for (int i = 0; i < int_1; i++) {
                    RenderSystem.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        RenderSystem.rotated(random.nextInt(10), 0, 1, 0);
                        RenderSystem.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
                    RenderSystem.popMatrix();
                }
            } else {
                RenderSystem.scaled(0.5, 0.5, 0.5);

                if (block instanceof ChestBlock || block instanceof EnderChestBlock)
                    RenderSystem.rotated(180, 0, 1, 0);

                RenderSystem.translated(-0.5, 0, 0.5);

                if (block instanceof BedBlock)
                    RenderSystem.translated(0.5, 0, 0);

                if (block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT) {
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.enableRescaleNormal();
                    RenderSystem.alphaFunc(516, 0.1F);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA.value, GlStateManager.class_4535.ONE_MINUS_SRC_ALPHA.value, GlStateManager.class_4535.ONE.value, GlStateManager.class_4535.ZERO.value);
                }

                for (int i = 0; i < int_1; i++) {
                    RenderSystem.pushMatrix();
                    if (i > 0) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        RenderSystem.rotated(random.nextInt(10), 0, 1, 0);
                        RenderSystem.translated(x * 2, y * 0.5, z * 2);
                    }
                    MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(block.getDefaultState(), 1);
                    RenderSystem.popMatrix();
                }

                if (block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT) {
                    RenderSystem.disableRescaleNormal();
                    RenderSystem.disableBlend();
                }
            }
        } else {
            Item item = stack.getItem();

            RenderSystem.translated(0, (0.5F / 16F), 0);
            RenderSystem.rotatef(90, 1, 0, 0);
            RenderSystem.scaled(0.6F, 0.6F, 0.6F);

            for (int i = 0; i < int_1; i++) {
                RenderSystem.pushMatrix();
                if (i > 0) {
                    float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float y = (random.nextFloat() * 2.0F) * 0.15F;
                    float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    RenderSystem.rotated(random.nextInt(10), 0, 1, 0);
                    RenderSystem.translated(x * 2, y * 0.5, z * 2);
                }
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
                RenderSystem.popMatrix();
            }
        }
    }
}
