package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.items.WrenchItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConveyanceItems {
    public static WrenchItem WRENCH = register("wrench", new WrenchItem(new Item.Settings().group(Conveyance.generalItemGroup)));

    public static void init() {
        // NO-OP
    }

    private static <T extends Item> T register(String name, T item) {
        Registry.register(Registry.ITEM, new Identifier(Conveyance.MODID, name), item);

        return item;
    }
}
