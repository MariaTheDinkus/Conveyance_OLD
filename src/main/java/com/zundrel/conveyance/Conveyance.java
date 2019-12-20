package com.zundrel.conveyance;

import com.zundrel.conveyance.common.registries.ConveyanceBlockEntities;
import com.zundrel.conveyance.common.registries.ConveyanceBlocks;
import com.zundrel.conveyance.common.registries.ConveyanceItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Conveyance implements ModInitializer {
    public static final String MODID = "conveyance";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ItemGroup generalItemGroup = FabricItemGroupBuilder.build(new Identifier(MODID, "general"), () -> new ItemStack(ConveyanceBlocks.CONVEYOR));

    public static HashMap<Item, Pair<Float, Boolean>> blacklistedBlocks = new HashMap<>();

    static {
        addBlacklistedBlock(Items.CHEST, true);
        addBlacklistedBlock(Items.TRAPPED_CHEST, true);
        addBlacklistedBlock(Items.ENDER_CHEST, true);
        addBlacklistedBlock(Items.CREEPER_HEAD, true);
        addBlacklistedBlock(Items.DRAGON_HEAD, 0.625F, true);
        addBlacklistedBlock(Items.PLAYER_HEAD, true);
        addBlacklistedBlock(Items.ZOMBIE_HEAD, true);
        addBlacklistedBlock(Items.REDSTONE, 0.8F, false);
    }

	@Override
	public void onInitialize() {
        ConveyanceItems.init();
        ConveyanceBlocks.init();
        ConveyanceBlockEntities.init();
	}

    public static void addBlacklistedBlock(Item item, boolean lifted) {
        addBlacklistedBlock(item, 1, lifted);
    }
	
	public static void addBlacklistedBlock(Item item, float scale, boolean lifted) {
        blacklistedBlocks.put(item, new Pair<>(scale, lifted));
    }
}
