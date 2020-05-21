package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.items.DurableItem;
import com.zundrel.conveyance.common.items.WrenchItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConveyanceItems {
    public static WrenchItem WRENCH = register("wrench", new WrenchItem(new Item.Settings().maxCount(1).group(Conveyance.generalItemGroup)));
	public static DurableItem HAMMER = register("hammer", new DurableItem(ToolMaterials.IRON, new Item.Settings().maxDamage(32).group(Conveyance.generalItemGroup)));
	//public static DurableItem WIRE_CUTTERS = register("wire_cutters", new DurableItem(ToolMaterials.IRON, new Item.Settings().maxDamage(32).group(Conveyance.generalItemGroup)));

    public static void init() {
        // NO-OP
    }

    private static <T extends Item> T register(String name, T item) {
        Registry.register(Registry.ITEM, new Identifier(Conveyance.MODID, name), item);

        return item;
    }
}
