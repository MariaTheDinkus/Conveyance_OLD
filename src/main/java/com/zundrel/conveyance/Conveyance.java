package com.zundrel.conveyance;

import com.zundrel.conveyance.common.registries.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Conveyance implements ModInitializer {
    public static final String MODID = "conveyance";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ItemGroup generalItemGroup = FabricItemGroupBuilder.build(new Identifier(MODID, "general"), () -> new ItemStack(ModBlocks.CONVEYOR));

	@Override
	public void onInitialize() {
        ModBlocks.init();
	}
}
