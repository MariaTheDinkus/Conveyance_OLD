package com.zundrel.conveyance.api;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.conveyors.ConveyorProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;

import java.util.Random;
import java.util.logging.Logger;

@Environment(EnvType.CLIENT)
public interface ConveyorRenderer<T extends BlockEntity> {
    default void renderSupport(T blockEntity, ConveyorType type, float position, float speed, float horizontalPosition, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        PositionalConveyable conveyor = (PositionalConveyable) blockEntity;
    	Direction direction = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
        int rotation = type == ConveyorType.DOWN_VERTICAL ? -90 : 90;

        matrixStack.push();

        matrixStack.translate(0.5, 4F / 16F, 0.5);

        if (type == ConveyorType.DOWN_VERTICAL) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
        }

        if (direction == Direction.NORTH && rotation == 90) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
        } else if (direction == Direction.SOUTH && rotation == -90) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
        } else if (direction == Direction.EAST) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        } else if (direction == Direction.WEST) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-rotation));
        }

        matrixStack.translate(-0.5F, -1.001F, -0.5F);

        if (type == ConveyorType.VERTICAL && blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) && conveyor.getPosition() == 16)
            matrixStack.translate(0, -1F / 16F, 0);

        if (type == ConveyorType.NORMAL) {
            matrixStack.translate(0, 0, position / speed);
        } else if (type == ConveyorType.VERTICAL) {
            matrixStack.translate(0, position / speed, horizontalPosition / speed);
        } else if (type == ConveyorType.DOWN_VERTICAL) {
            matrixStack.translate(0, (position / (speed)) + (blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) ? 1 : 0), horizontalPosition / speed);
        }

        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier(new Identifier(Conveyance.MODID, "conveyor_supports"),  ""));

        int light = LightmapTextureManager.pack(blockEntity.getWorld().getLightLevel(LightType.BLOCK, blockEntity.getPos()), blockEntity.getWorld().getLightLevel(LightType.SKY, blockEntity.getPos()));
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(RenderLayer.getCutout()), null, model, blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ(), light, OverlayTexture.DEFAULT_UV);

        matrixStack.pop();
    }

    default void renderItem(T blockEntity, ItemStack stack, float position, int speed, float horizontalPosition, ConveyorType type, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        Random random = new Random();
        Direction direction = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
        int rotation = type == ConveyorType.DOWN_VERTICAL ? -90 : 90;
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

        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && !Conveyance.blacklistedBlocks.containsKey(stack.getItem())) {
            int light = LightmapTextureManager.pack(blockEntity.getWorld().getLightLevel(LightType.BLOCK, blockEntity.getPos()), blockEntity.getWorld().getLightLevel(LightType.SKY, blockEntity.getPos()));
            Block block = ((BlockItem) stack.getItem()).getBlock();

            for (int i = 0; i < int_1; i++) {
                matrixStack.push();
                matrixStack.translate(0.5F, 4F / 16F, 0.5F);
                if (direction == Direction.NORTH && rotation == 90) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
                } else if (direction == Direction.SOUTH && rotation == -90) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
                } else if (direction == Direction.EAST) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
                } else if (direction == Direction.WEST) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-rotation));
                }

                if (type == ConveyorType.NORMAL) {
                    matrixStack.translate(0, 0, position / speed);
                } else if (type == ConveyorType.VERTICAL) {
                    matrixStack.translate(0, position / speed, horizontalPosition / speed);
                } else if (type == ConveyorType.DOWN_VERTICAL) {
                    matrixStack.translate(0, (position / (speed)) + (blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) ? 1 : 0), horizontalPosition / speed);
                }
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.translate(-0.5F, 0, -0.5F);

                if (i > 0) {
                    float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float y = (random.nextFloat() * 2.0F) * 0.15F;
                    float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrixStack.translate(x * 2, y * 0.5F, z * 2);
                }

                try {
                    MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getDefaultState(), matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
                    if (stack.getItem() instanceof TallBlockItem) {
                        matrixStack.translate(0, 1, 0);
                        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getDefaultState().with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
                    }
                } catch (Exception e) {
                    Logger.getGlobal().warning(e.getMessage());
                }

                matrixStack.pop();
            }
        } else if (!stack.isEmpty()) {
            int light = LightmapTextureManager.pack(blockEntity.getWorld().getLightLevel(LightType.BLOCK, blockEntity.getPos()), blockEntity.getWorld().getLightLevel(LightType.SKY, blockEntity.getPos()));

            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            matrixStack.translate(0.5, 0.5, -4.5F / 16F);

            if (stack.getItem() instanceof BlockItem && Conveyance.blacklistedBlocks.containsKey(stack.getItem()) && Conveyance.blacklistedBlocks.get(stack.getItem()).getRight()) {
                matrixStack.translate(0, 0, -3.5F / 16F);
            }

            if (direction == Direction.NORTH && rotation == 90) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
            } else if (direction == Direction.SOUTH && rotation == -90) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
            } else if (direction == Direction.EAST) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-rotation));
            } else if (direction == Direction.WEST) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            }

            if (type == ConveyorType.NORMAL) {
                matrixStack.translate(0, position / speed, 0);
            } else if (type == ConveyorType.VERTICAL) {
                matrixStack.translate(0, horizontalPosition / speed, -position / speed);
            } else if (type == ConveyorType.DOWN_VERTICAL) {
                matrixStack.translate(0, horizontalPosition / speed, -(position / (speed)) + (blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) ? -1 : 0));
            }

            if (Conveyance.blacklistedBlocks.containsKey(stack.getItem())) {
                float scale = Conveyance.blacklistedBlocks.get(stack.getItem()).getLeft();
                matrixStack.scale(scale, scale, scale);

                for (int i = 1; i < int_1; i++) {
                    matrixStack.push();
                    if (Conveyance.blacklistedBlocks.get(stack.getItem()).getRight()) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        matrixStack.translate(x, z, y * 0.5F);
                    }
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
                    matrixStack.pop();
                }
            } else {
                matrixStack.scale(0.8F, 0.8F, 0.8F);
            }

            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
            matrixStack.pop();
        }
    }

	default void renderItem(T blockEntity, ItemStack stack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
    	if (!stack.isEmpty()) {
			int light = LightmapTextureManager.pack(blockEntity.getWorld().getLightLevel(LightType.BLOCK, blockEntity.getPos()), blockEntity.getWorld().getLightLevel(LightType.SKY, blockEntity.getPos()));

			matrixStack.push();
			if (!(stack.getItem() instanceof BlockItem))
				matrixStack.scale(0.8F, 0.8F, 0.8F);
			MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
			matrixStack.pop();
		}
	}

    default void renderItem(T blockEntity, Direction direction, ItemStack stack, float position, int speed, float horizontalPosition, ConveyorType type, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        Random random = new Random();
        int rotation = type == ConveyorType.DOWN_VERTICAL ? -90 : 90;
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

        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && !Conveyance.blacklistedBlocks.containsKey(stack.getItem())) {
			int light = LightmapTextureManager.pack(blockEntity.getWorld().getLightLevel(LightType.BLOCK, blockEntity.getPos().offset(direction)), blockEntity.getWorld().getLightLevel(LightType.SKY, blockEntity.getPos().offset(direction)));
            Block block = ((BlockItem) stack.getItem()).getBlock();

            for (int i = 0; i < int_1; i++) {
                matrixStack.push();
                matrixStack.translate(0.5F, 4F / 16F, 0.5F);
                if (direction == Direction.NORTH && rotation == 90) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
                } else if (direction == Direction.SOUTH && rotation == -90) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
                } else if (direction == Direction.EAST) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
                } else if (direction == Direction.WEST) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-rotation));
                }

                if (type == ConveyorType.NORMAL) {
                    matrixStack.translate(0, 0, position / speed);
                } else if (type == ConveyorType.VERTICAL) {
                    matrixStack.translate(0, position / speed, horizontalPosition / speed);
                } else if (type == ConveyorType.DOWN_VERTICAL) {
                    matrixStack.translate(0, (position / (speed)) + (blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) ? 1 : 0), horizontalPosition / speed);
                }
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.translate(-0.5F, 0, -0.5F);

                if (i > 0) {
                    float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float y = (random.nextFloat() * 2.0F) * 0.15F;
                    float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrixStack.translate(x * 2, y * 0.5F, z * 2);
                }

                MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getDefaultState(), matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
                if (stack.getItem() instanceof TallBlockItem) {
                    matrixStack.translate(0, 1, 0);
                    MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getDefaultState().with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
                }

                matrixStack.pop();
            }
        } else if (!stack.isEmpty()) {
            int light = LightmapTextureManager.pack(blockEntity.getWorld().getLightLevel(LightType.BLOCK, blockEntity.getPos()), blockEntity.getWorld().getLightLevel(LightType.SKY, blockEntity.getPos()));

            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            matrixStack.translate(0.5, 0.5, -4.5F / 16F);

            if (stack.getItem() instanceof BlockItem && Conveyance.blacklistedBlocks.containsKey(stack.getItem()) && Conveyance.blacklistedBlocks.get(stack.getItem()).getRight()) {
                matrixStack.translate(0, 0, -3.5F / 16F);
            }

            if (direction == Direction.NORTH && rotation == 90) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
            } else if (direction == Direction.SOUTH && rotation == -90) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
            } else if (direction == Direction.EAST) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-rotation));
            } else if (direction == Direction.WEST) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            }

            if (type == ConveyorType.NORMAL) {
                matrixStack.translate(0, position / speed, 0);
            } else if (type == ConveyorType.VERTICAL) {
                matrixStack.translate(0, horizontalPosition / speed, -position / speed);
            } else if (type == ConveyorType.DOWN_VERTICAL) {
                matrixStack.translate(0, horizontalPosition / speed, -(position / (speed)) + (blockEntity.getCachedState().get(ConveyorProperties.CONVEYOR) ? -1 : 0));
            }

            if (Conveyance.blacklistedBlocks.containsKey(stack.getItem())) {
                float scale = Conveyance.blacklistedBlocks.get(stack.getItem()).getLeft();
                matrixStack.scale(scale, scale, scale);

                for (int i = 1; i < int_1; i++) {
                    matrixStack.push();
                    if (Conveyance.blacklistedBlocks.get(stack.getItem()).getRight()) {
                        float x = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (random.nextFloat() * 2.0F) * 0.15F;
                        float z = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        matrixStack.translate(x, z, y * 0.5F);
                    }
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
                    matrixStack.pop();
                }
            } else {
                matrixStack.scale(0.8F, 0.8F, 0.8F);
            }

            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
            matrixStack.pop();
        }
    }
}
