package com.zundrel.conveyance.api;

import net.minecraft.util.StringIdentifiable;

public enum ConveyorType implements StringIdentifiable {
    NORMAL(""),
    VERTICAL(""),
    DOWN_VERTICAL("");

    String name;

    ConveyorType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
