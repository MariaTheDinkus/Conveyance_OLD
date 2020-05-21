package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public class ConveyanceArtisTables {
	public static ArtisTableType METALWORKING_TABLE = register(new ArtisTableType(new Identifier(Conveyance.MODID, "metalworking_table"), 3, 3, true, true, true, 0xA1A1A1), FabricBlockSettings.copy(Blocks.CRAFTING_TABLE).nonOpaque());

    public static void init() {
        // NO-OP
    }

    private static <T extends ArtisTableType> T register(T table, AbstractBlock.Settings settings) {
		Artis.registerTable(table, settings);
        return table;
    }
}
